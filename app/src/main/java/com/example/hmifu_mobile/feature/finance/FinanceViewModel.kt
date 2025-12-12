package com.example.hmifu_mobile.feature.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.TransactionEntity
import com.example.hmifu_mobile.data.local.entity.TransactionType
import com.example.hmifu_mobile.data.repository.FinanceRepository
import com.example.hmifu_mobile.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FinanceUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val isTreasurer: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        checkRole()
        loadData()
        syncData()
    }

    private fun checkRole() {
        viewModelScope.launch {
            val result = userRepository.syncCurrentUser()
            result.onSuccess { profile ->
                val isTreasurer = profile.role.lowercase() == "treasurer"
                _uiState.update { it.copy(isTreasurer = isTreasurer) }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                financeRepository.observeAll(),
                financeRepository.observeTotalIncome(),
                financeRepository.observeTotalExpense()
            ) { transactions, income, expense ->
                Triple(transactions, income ?: 0.0, expense ?: 0.0)
            }.collect { (transactions, income, expense) ->
                _uiState.update {
                    it.copy(
                        transactions = transactions,
                        totalIncome = income,
                        totalExpense = expense,
                        balance = income - expense,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            financeRepository.syncFromFirestore().collect { result ->
                result.onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }

    fun addTransaction(
        amount: Double,
        type: TransactionType,
        description: String,
        category: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = userRepository.currentUserId ?: return@launch
            val userEmail = userRepository.currentUserEmail ?: "Unknown"

            // Ideally fetch name from repository cache
            val authorName = userEmail 

            financeRepository.addTransaction(
                amount = amount,
                type = type,
                description = description,
                category = category,
                authorId = userId,
                authorName = authorName
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Transaction added successfully"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
