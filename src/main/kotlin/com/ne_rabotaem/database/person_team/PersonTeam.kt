package com.ne_rabotaem.database.person_team

import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.user.User
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object PersonTeam : IntIdTable("Person_team") {
    val personId = reference("person_id", User.id)
    val teamId = reference("team_id", Team.id)

    fun insert(personTeamDTO: PersonTeamDTO) {
        transaction {
            insert {
                it[personId] = personTeamDTO.personId
                it[teamId] = personTeamDTO.teamId
            }
        }
    }

    fun getTeam(personId: Int): Int? {
        return transaction {
            select { PersonTeam.personId eq personId }.firstOrNull()?.get(teamId)?.value
        }
    }

    fun getMembers(teamId: Int): List<Int> {
        return transaction {
            select { PersonTeam.teamId eq teamId }.toList().map {
                it[personId].value
            }
        }
    }

    fun delete(personId: Int) {
        transaction {
            val teamId: Int = select { PersonTeam.personId eq personId }.single()[teamId].value
            deleteWhere { PersonTeam.personId eq personId and (PersonTeam.teamId eq teamId) }
        }
    }
}
