package com.ne_rabotaem.database.event

import com.ne_rabotaem.database.user.PGEnum
import com.ne_rabotaem.features.demo.EventResponseRemote
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

enum class EventType {
    demo,
    _360
}

object Event : IntIdTable("Event") {
    private val type = customEnumeration("type", "event", { value -> EventType.valueOf(value as String) }, { PGEnum("event_type", it) })
    val date = date("date")
    private val start = time("start")
    private val finish = time("finish")


    fun insert(eventDTO: EventDTO) {
        transaction {
            insert {
                it[type] = eventDTO.type
                it[date] = eventDTO.date
                it[start] = eventDTO.start
                it[finish] = eventDTO.finish
            }
        }
    }

    fun fetch(eventId: Int): EventDTO? {
        return try {
            transaction {
                val eventModel = select { Event.id.eq(eventId) }.single()
                EventDTO(
                    eventId = eventModel[Event.id].value,
                    type = eventModel[type],
                    date = eventModel[date],
                    start = eventModel[start],
                    finish = eventModel[finish],
                )
            }
        } catch (e: Exception) {
            when(e) {
                is NoSuchElementException, is IllegalArgumentException -> null
                else -> {
                    throw e
                }
            }
        }
    }

    fun fetchAll(): List<EventResponseRemote> {
        return transaction {
                Event.selectAll().toList().map { EventResponseRemote(it[Event.id].value, it[type], it[date], it[start], it[finish]) }
        }
    }
}
