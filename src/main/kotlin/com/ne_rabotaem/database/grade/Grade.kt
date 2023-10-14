package com.ne_rabotaem.database.grade

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.features.demo.GradeResponseRemote
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Demo_grade : IntIdTable("Demo_grade") {
    private val eventId = reference("event_id", Event.id)
    private val personId = reference("person_id", User.id)
    private val teamId = reference("team_id", Team.id)
    private val grade = integer("grade")
    private val comment = varchar("comment", 500)

    fun insert(gradeDTO: GradeDTO) {
        throw NotImplementedError()
    }

    fun fetch(eventId: Int): List<GradeResponseRemote> {
        return transaction {
            Demo_grade.select { Demo_grade.eventId.eq(eventId) }.toList().map {
                GradeResponseRemote(
                    it[Demo_grade.id].value,
                    it[personId].value,
                    it[teamId].value,
                    it[grade],
                    it[comment],
                )
            }
        }
    }
}
