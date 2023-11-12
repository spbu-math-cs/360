package com.ne_rabotaem.database.person_team

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.user.User
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object InteamGrade : IntIdTable("Inteam_grade") {
    private val eventId = reference("event_id", Event.id)
    private val evaluatorId = reference("evaluator_id", User.id)
    private val assessedId = reference("assessed_id", User.id)

    fun insert(inteamTeamDTO: InteamGradeDTO) {
        transaction {
            insert {
                it[eventId] = inteamTeamDTO.eventId
                it[evaluatorId] = inteamTeamDTO.evaluatorId
                it[assessedId] = inteamTeamDTO.assessedId
            }
        }
    }

    fun update(gradeId: Int, inteamGradeDTO: InteamGradeDTO) {
        transaction {
            update( { Invite.id eq gradeId } ) {
                it[eventId] = inteamGradeDTO.eventId
                it[evaluatorId] = inteamGradeDTO.evaluatorId
                it[assessedId] = inteamGradeDTO.assessedId
            }
        }
    }
}
