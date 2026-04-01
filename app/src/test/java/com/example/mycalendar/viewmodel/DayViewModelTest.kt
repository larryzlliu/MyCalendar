package com.example.mycalendar.viewmodel

import androidx.lifecycle.SavedStateHandle
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
class DayViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = CalendarRepository()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(date: LocalDate): DayViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("date" to date.toString()))
        return DayViewModel(savedStateHandle, repository)
    }

    @Test
    fun `date is parsed correctly from SavedStateHandle`() {
        val date = LocalDate.of(2026, 4, 10)
        assertEquals(date, createViewModel(date).date)
    }

    @Test
    fun `loads events for date with one event`() {
        val viewModel = createViewModel(LocalDate.of(2026, 4, 10))
        val state = viewModel.uiState.value as DayUiState.Displaying
        assertEquals(1, state.events.size)
        assertEquals("Lunch with Jenny", state.events[0].title)
    }

    @Test
    fun `loads empty events for date with no events`() {
        val viewModel = createViewModel(LocalDate.of(2026, 4, 2))
        val state = viewModel.uiState.value as DayUiState.Displaying
        assertTrue(state.events.isEmpty())
    }

    @Test
    fun `loads correct event on April 1`() {
        val viewModel = createViewModel(LocalDate.of(2026, 4, 1))
        val state = viewModel.uiState.value as DayUiState.Displaying
        assertEquals("Team Standup", state.events[0].title)
    }

    @Test
    fun `loads correct event on April 30`() {
        val viewModel = createViewModel(LocalDate.of(2026, 4, 30))
        val state = viewModel.uiState.value as DayUiState.Displaying
        assertEquals("End of Month Review", state.events[0].title)
    }
}
