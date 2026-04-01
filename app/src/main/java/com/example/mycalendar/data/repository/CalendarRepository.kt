package com.example.mycalendar.data.repository

import com.example.mycalendar.data.model.Event
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepository @Inject constructor() {

    private val events = listOf(
        Event(
            id = 1,
            title = "Team Standup",
            date = LocalDate.of(2026, 4, 1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(9, 30)
        ),
        Event(
            id = 2,
            title = "Sprint Planning",
            date = LocalDate.of(2026, 4, 7),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 30)
        ),
        Event(
            id = 3,
            title = "Dentist Appointment",
            date = LocalDate.of(2026, 4, 9),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0)
        ),
        Event(
            id = 4,
            title = "Lunch with Jenny",
            date = LocalDate.of(2026, 4, 10),
            startTime = LocalTime.of(12, 0),
            endTime = LocalTime.of(13, 0)
        ),
        Event(
            id = 5,
            title = "Design Review",
            date = LocalDate.of(2026, 4, 14),
            startTime = LocalTime.of(15, 0),
            endTime = LocalTime.of(16, 0)
        ),
        Event(
            id = 6,
            title = "Doctor Checkup",
            date = LocalDate.of(2026, 4, 16),
            startTime = LocalTime.of(11, 0),
            endTime = LocalTime.of(12, 0)
        ),
        Event(
            id = 7,
            title = "Team Retrospective",
            date = LocalDate.of(2026, 4, 21),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0)
        ),
        Event(
            id = 8,
            title = "Coffee with Alex",
            date = LocalDate.of(2026, 4, 24),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(10, 30)
        ),
        Event(
            id = 9,
            title = "Product Demo",
            date = LocalDate.of(2026, 4, 28),
            startTime = LocalTime.of(13, 0),
            endTime = LocalTime.of(14, 30)
        ),
        Event(
            id = 10,
            title = "End of Month Review",
            date = LocalDate.of(2026, 4, 30),
            startTime = LocalTime.of(16, 0),
            endTime = LocalTime.of(17, 0)
        )
    )

    fun getEventsForDate(date: LocalDate): List<Event> =
        events.filter { it.date == date }

    fun getEventsForMonth(year: Int, month: Int): List<Event> =
        events.filter { it.date.year == year && it.date.monthValue == month }
}
