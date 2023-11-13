package com.ne_rabotaem.database.person_team

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.user.User
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object InteamGrade : IntIdTable("Inteam_grade") {
    private val eventId = reference("event_id", Event.id)
    private val evaluatorId = reference("evaluator_id", User.id)
    private val assessedId = reference("assessed_id", User.id)
    private val grade = integer("grade")

    fun insertOrUpdate(inteamTeamDTO: InteamGradeDTO) {
        transaction {
            val id = getId(inteamTeamDTO.eventId, inteamTeamDTO.evaluatorId, inteamTeamDTO.assessedId)

            if (id != null) {
                update ({ InteamGrade.id eq id }) {
                    it[grade] = inteamTeamDTO.grade
                }
                return@transaction
            }

            insert {
                it[eventId] = inteamTeamDTO.eventId
                it[evaluatorId] = inteamTeamDTO.evaluatorId
                it[assessedId] = inteamTeamDTO.assessedId
                it[grade] = inteamTeamDTO.grade
            }
        }
    }

    fun getId(evendId: Int, evaluatorId: Int, assessedId: Int): Int? {
        return transaction {
            select { InteamGrade.eventId eq evendId and
                    (InteamGrade.evaluatorId eq evaluatorId) and
                    (InteamGrade.assessedId eq assessedId) }.single()[InteamGrade.id].value
        }
    }

    fun update(gradeId: Int, grade: Int) {
        transaction {
            update( { Invite.id eq gradeId } ) {
                it[InteamGrade.grade] = grade
            }
        }
    }
}
