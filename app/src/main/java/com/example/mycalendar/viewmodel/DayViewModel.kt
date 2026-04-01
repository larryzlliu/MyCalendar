package com.example.mycalendar.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycalendar.data.model.Event
import com.example.mycalendar.data.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CalendarRepository
) : ViewModel() {

    val date: LocalDate = LocalDate.parse(checkNotNull(savedStateHandle["date"]))

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
}

sealed interface DayUiState {
    data object Loading : DayUiState
    data class Displaying(val events: List<Event>) : DayUiState
    data class Error(val errorMessage: String) : DayUiState
}
