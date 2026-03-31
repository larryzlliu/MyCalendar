package com.example.mycalendar.data.model

import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: Long,
    val title: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime
)
