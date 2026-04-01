package com.example.mycalendar.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CalendarRepositoryTest {

    private lateinit var repository: CalendarRepository

    @Before
    fun setup() {
        repository = CalendarRepository()
    }

    @Test
    fun `getEventsForMonth returns all 10 April 2026 events`() {
        val events = repository.getEventsForMonth(2026, 4)
        assertEquals(10, events.size)
    }

    @Test
    fun `getEventsForMonth returns empty for month with no events`() {
        val events = repository.getEventsForMonth(2026, 3)
        assertTrue(events.isEmpty())
    }

    @Test
    fun `getEventsForDate returns event on April 1`() {
        val events = repository.getEventsForDate(LocalDate.of(2026, 4, 1))
        assertEquals(1, events.size)
        assertEquals("Team Standup", events[0].title)
    }

    @Test
    fun `getEventsForDate returns empty for date with no events`() {
        val events = repository.getEventsForDate(LocalDate.of(2026, 4, 2))
        assertTrue(events.isEmpty())
    }

    @Test
    fun `getEventsForDate returns correct event on April 10`() {
        val events = repository.getEventsForDate(LocalDate.of(2026, 4, 10))
        assertEquals(1, events.size)
        assertEquals("Lunch with Jenny", events[0].title)
    }

    @Test
    fun `getEventsForDate returns correct start and end times`() {
        val event = repository.getEventsForDate(LocalDate.of(2026, 4, 7)).first()
        assertEquals("Sprint Planning", event.title)
        assertEquals(10, event.startTime.hour)
        assertEquals(11, event.endTime.hour)
        assertEquals(30, event.endTime.minute)
    }
}
