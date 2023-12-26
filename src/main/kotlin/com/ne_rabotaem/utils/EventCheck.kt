package com.ne_rabotaem.utils

import com.ne_rabotaem.database.event.EventDTO
import java.time.ZoneId
import java.time.ZonedDateTime

object EventCheck {
    fun isDemoValid(eventDTO: EventDTO): Boolean {
        val dateTime = ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Europe/Moscow"))
        return eventDTO.start < dateTime.toLocalTime() && eventDTO.finish > dateTime.toLocalTime() && eventDTO.date == dateTime.toLocalDate()
    }

    fun isDemoFinished(eventDTO: EventDTO) :Boolean {
        val dateTime = ZonedDateTime.now(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Europe/Moscow"))
        return eventDTO.finish < dateTime.toLocalTime() && eventDTO.date == dateTime.toLocalDate() || eventDTO.date < dateTime.toLocalDate()
    }
}