package com.example.hmifu_mobile.feature.admin.treasurer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.TransactionEntity
import com.example.hmifu_mobile.data.local.entity.TransactionType
import com.example.hmifu_mobile.data.repository.TransactionRepository
import com.example.hmifu_mobile.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TreasurerUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val totalBalance: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class TreasurerViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TreasurerUiState())
    val uiState: StateFlow<TreasurerUiState> = _uiState.asStateFlow()

    init {
        loadData()
        startSync()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                transactionRepository.observeTransactions(),
                transactionRepository.observeTotalIncome(),
                transactionRepository.observeTotalExpense()
            ) { transactions, income, expense ->
                Triple(transactions, income ?: 0.0, expense ?: 0.0)
            }.collect { (list, income, expense) ->
                _uiState.update {
                    it.copy(
                        transactions = list,
                        totalIncome = income,
                        totalExpense = expense,
                        totalBalance = income - expense,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun startSync() {
        viewModelScope.launch {
            transactionRepository.syncFromFirestore().collect { result ->
                result.onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }

    fun addTransaction(type: TransactionType, amount: Double, description: String, category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val user = userRepository.observeCurrentUser().firstOrNull()
            
            if (user == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "User not found") }
                return@launch
            }

            val result = transactionRepository.createTransaction(type, amount, description, category, user)
            
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                // Reset success flag after short delay or consumed by UI
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun clearSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
