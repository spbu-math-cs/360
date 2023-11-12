package com.ne_rabotaem

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.event.EventDTO
import com.ne_rabotaem.database.event.EventType
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SampleTest {
    @BeforeTest
    fun prepare() {
        Database.connect(
            "jdbc:postgresql://localhost:5432/DEV_ne_rabotaem",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "dubchuk",
        )
    }

    @Test
    fun fetchTest() {
        var date = LocalDate.now()
        Event.insert(
            EventDTO(
                eventId = 0,
                type = EventType.demo,
                date = date,
                start = LocalTime.now(),
                finish = LocalTime.now().plusHours(2)
            )
        )

        var eventId: Int = -1
        transaction {
            eventId = Event.select { Event.date eq date }.first().run {
                this[Event.id].value
            }
        }
        assertEquals(Event.fetch(eventId)?.date, date)
    }
}
