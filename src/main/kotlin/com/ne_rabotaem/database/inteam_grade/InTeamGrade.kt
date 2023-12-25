package com.ne_rabotaem.database.person_team

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.grade.DemoGrade
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.features.vote.PersonDemoGradeResponseRemote
import com.ne_rabotaem.features.vote.PersonInTeamVotingResponseRemote
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.avg
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object InTeamGrade : IntIdTable("Inteam_grade") {
    private val eventId = reference("event_id", Event.id)
    private val evaluatorId = reference("evaluator_id", User.id)
    private val assessedId = reference("assessed_id", User.id)
    private val grade = integer("grade")

    fun insertOrUpdate(inteamTeamDTO: InteamGradeDTO) {
        transaction {
            val id = getId(inteamTeamDTO.eventId, inteamTeamDTO.evaluatorId, inteamTeamDTO.assessedId)

            if (id != null) {
                update ({ InTeamGrade.id eq id }) {
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
            select { InTeamGrade.eventId eq evendId and
                    (InTeamGrade.evaluatorId eq evaluatorId) and
                    (InTeamGrade.assessedId eq assessedId) }.singleOrNull()?.get(InTeamGrade.id)?.value
        }
    }

    fun getGrades(personId: Int, eventId: Int): List<PersonInTeamVotingResponseRemote> {
        return transaction {
            select {
                evaluatorId eq personId and (InTeamGrade.eventId eq eventId)
            }.toList().map {
                PersonInTeamVotingResponseRemote(
                    personId = it[assessedId].value,
                    grade = it[grade]
                )
            }
        }
    }

    fun getDemoUserRating(userId: Int, eventId: Int): Double {
        return transaction {
            slice(grade.avg())
                .select { assessedId eq userId and (InTeamGrade.eventId eq eventId) }
                .toList()
                .single()[grade.avg()]?.toDouble() ?: 0.0
        }
    }

    fun getDemoAvgRating(eventId: Int, userId: Int): Double {
        val teamId = PersonTeam.getTeam(userId)

        return transaction {
            Join(InTeamGrade,
                PersonTeam,
                onColumn = InTeamGrade.assessedId,
                otherColumn = PersonTeam.personId)
                .slice(grade.avg())
                .select { InTeamGrade.eventId eq eventId and (PersonTeam.teamId eq teamId) }
                .toList()
                .single()[grade.avg()]?.toDouble() ?: 1.0
        }
    }

    fun update(gradeId: Int, grade: Int) {
        transaction {
            update( { Invite.id eq gradeId } ) {
                it[InTeamGrade.grade] = grade
            }
        }
    }
}
