package com.ne_rabotaem.database.event
import java.time.LocalDate
import java.time.LocalTime

class EventDTO(
    val eventId: Int,
    val type: EventType,
    val date: LocalDate,
    val start: LocalTime,
    val finish: LocalTime
)