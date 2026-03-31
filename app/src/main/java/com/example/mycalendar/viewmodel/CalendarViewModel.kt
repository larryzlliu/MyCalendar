package com.example.mycalendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycalendar.data.model.Event
import com.example.mycalendar.data.repository.CalendarRepository
import com.example.mycalendar.viewmodel.CalendarUiState.Displaying
import com.example.mycalendar.viewmodel.CalendarUiState.Loading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(
    private val repository: CalendarRepository = CalendarRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<CalendarUiState>(Loading)
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadCalendar()
    }

    private fun loadCalendar() {
        viewModelScope.launch {
            val events = repository.getEventsForMonth(
                LocalDate.now().year,
                LocalDate.now().monthValue
            )
            _uiState.value = Displaying(events = events)
        }
    }

    fun onDateSelected(date: LocalDate) {
        val current = _uiState.value
        if (current is Displaying) {
            _uiState.value = current.copy(selectedDate = date)
        }
    }

    fun onMonthChanged(date: LocalDate) {
        val current = _uiState.value
        if (current is Displaying) {
            val events = repository.getEventsForMonth(date.year, date.monthValue)
            _uiState.value = current.copy(currentMonth = date, events = events)
        }
    }
}

sealed interface CalendarUiState {
    data object Loading : CalendarUiState
    data class Displaying(
        val currentMonth: LocalDate = LocalDate.now(),
        val selectedDate: LocalDate = LocalDate.now(),
        val events: List<Event> = emptyList()
    ) : CalendarUiState
    data class Error(val errorMessage: String) : CalendarUiState
}
