package com.example.mycalendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mycalendar.data.model.Event
import com.example.mycalendar.data.repository.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DayViewModel(
    val date: LocalDate,
    private val repository: CalendarRepository = CalendarRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DayUiState>(DayUiState.Loading)
    val uiState: StateFlow<DayUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            val events = repository.getEventsForDate(date)
            _uiState.value = DayUiState.Displaying(events = events)
        }
    }

    class Factory(private val date: LocalDate) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DayViewModel(date) as T
        }
    }
}

sealed interface DayUiState {
    data object Loading : DayUiState
    data class Displaying(val events: List<Event>) : DayUiState
    data class Error(val errorMessage: String) : DayUiState
}
