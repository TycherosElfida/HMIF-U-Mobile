package com.example.hmifu_mobile.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmifu_mobile.data.local.entity.AnnouncementCategory
import com.example.hmifu_mobile.data.local.entity.AnnouncementEntity
import com.example.hmifu_mobile.data.local.entity.EventEntity
import com.example.hmifu_mobile.data.repository.AnnouncementRepository
import com.example.hmifu_mobile.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CategoryFeedUiState(
    val categoryName: String = "",
    val categories: List<AnnouncementCategory> = emptyList(), // Filter for just this one usually
    val announcements: List<AnnouncementEntity> = emptyList(),
    val events: List<EventEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoryFeedViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val eventRepository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryType: String = checkNotNull(savedStateHandle["categoryType"])
    private val categoryEnum = AnnouncementCategory.entries.find { it.name == categoryType } 
        ?: AnnouncementCategory.GENERAL

    val uiState: StateFlow<CategoryFeedUiState> = combine(
        announcementRepository.observeAnnouncements(),
        eventRepository.observeEvents()
    ) { announcements, events ->
        val filteredAnnouncements = announcements.filter { it.category == categoryType }
        // For events, we might not have a direct category mapping yet, or valid "Category" field.
        // Assuming events might be relevant to competitions if titles say so, or just show all for now?
        // Actually the user said: "announcement and event with category competition".
        // EventEntity doesn't have a category field yet in my memory (checked earlier).
        // Let's filter events by simple title contains for now or show none if not sure.
        // Wait, Announcement has category. Event might not.
        // Let's filter events if the category is COMPETITION.
        
        val filteredEvents = if (categoryType == "COMPETITION") {
             events.filter { 
                 it.category.equals("COMPETITION", ignoreCase = true) ||
                 it.title.contains("Lomba", ignoreCase = true) ||
                 it.title.contains("Hackathon", ignoreCase = true)
             }
        } else if (categoryType == "CAREER") {
             events.filter { 
                 it.category.equals("SEMINAR", ignoreCase = true) || // Seminars often career related
                 it.title.contains("Career", ignoreCase = true) ||
                 it.title.contains("Intern", ignoreCase = true) ||
                 it.title.contains("Job", ignoreCase = true)
             }
        } else {
            emptyList()
        }

        CategoryFeedUiState(
            categoryName = categoryEnum.displayName,
            announcements = filteredAnnouncements,
            events = filteredEvents,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryFeedUiState(categoryName = categoryEnum.displayName)
    )
}
