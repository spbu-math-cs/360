package com.ne_rabotaem.database.grade

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.features.demo.GradeResponseRemote
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Demo_grade : IntIdTable("Demo_grade") {
    private val eventId = reference("event_id", Event.id)
    private val personId = reference("person_id", User.id)
    private val teamId = reference("team_id", Team.id)
    private val level = integer("level")
    private val grade = integer("grade")
    private val presentation = integer("presentation")
    private val additional = integer("additional")
    private val comment = varchar("comment", 500)

    fun insert(gradeDTO: GradeDTO) {
        transaction {
            insert {
                it[eventId] = gradeDTO.eventId
                it[personId] = gradeDTO.personId
                it[teamId] = gradeDTO.teamId
                it[level] = gradeDTO.level
                it[grade] = gradeDTO.grade
                it[presentation] = gradeDTO.presentation
                it[additional] = gradeDTO.additional
                it[comment] = gradeDTO.comment.orEmpty()
            }
        }
    }

    fun fetch(eventId: Int): List<GradeResponseRemote> {
        return transaction {
            Demo_grade.select { Demo_grade.eventId.eq(eventId) }.toList().map {
                GradeResponseRemote(
                    it[Demo_grade.id].value,
                    it[personId].value,
                    it[teamId].value,
                    it[level],
                    it[grade],
                    it[presentation],
                    it[additional],
                    it[comment],
                )
            }
        }
    }

    fun fetch(eventId: Int, userId: Int, teamId: Int): GradeDTO? {
        return transaction {
            val query = Demo_grade.select {
                Demo_grade.eventId.eq(eventId) and Demo_grade.personId.eq(userId) and Demo_grade.teamId.eq(teamId)
            }
            if (query.none())
                return@transaction null

            query.single().run {
                GradeDTO(
                    eventId,
                    userId,
                    teamId,
                    this[level],
                    this[grade],
                    this[presentation],
                    this[additional],
                    this[comment]
                )
            }
        }
    }
}
