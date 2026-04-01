package com.example.mycalendar.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import com.example.mycalendar.data.repository.CalendarRepository
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: CalendarViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CalendarViewModel(CalendarRepository())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Displaying after init`() {
        assertTrue(viewModel.uiState.value is CalendarUiState.Displaying)
    }

    @Test
    fun `onDateSelected updates selectedDate`() {
        val date = LocalDate.of(2026, 4, 10)
        viewModel.onDateSelected(date)
        val state = viewModel.uiState.value as CalendarUiState.Displaying
        assertEquals(date, state.selectedDate)
    }

    @Test
    fun `onMonthChanged updates currentMonth`() {
        val april = LocalDate.of(2026, 4, 1)
        viewModel.onMonthChanged(april)
        val state = viewModel.uiState.value as CalendarUiState.Displaying
        assertEquals(april, state.currentMonth)
    }

    @Test
    fun `onMonthChanged to April 2026 loads 10 events`() {
        viewModel.onMonthChanged(LocalDate.of(2026, 4, 1))
        val state = viewModel.uiState.value as CalendarUiState.Displaying
        assertEquals(10, state.events.size)
    }

    @Test
    fun `onMonthChanged to month with no events returns empty list`() {
        viewModel.onMonthChanged(LocalDate.of(2026, 3, 1))
        val state = viewModel.uiState.value as CalendarUiState.Displaying
        assertTrue(state.events.isEmpty())
    }

    @Test
    fun `onDateSelected does not change currentMonth`() {
        viewModel.onMonthChanged(LocalDate.of(2026, 4, 1))
        val monthBefore = (viewModel.uiState.value as CalendarUiState.Displaying).currentMonth
        viewModel.onDateSelected(LocalDate.of(2026, 4, 15))
        val monthAfter = (viewModel.uiState.value as CalendarUiState.Displaying).currentMonth
        assertEquals(monthBefore, monthAfter)
    }
}
