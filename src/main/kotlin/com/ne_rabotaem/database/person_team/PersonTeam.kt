package com.ne_rabotaem.database.person_team

import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.features.profile.TeammateResponseRemote
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Join
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

    fun getMembers(teamId: Int): List<TeammateResponseRemote> {
        return transaction {
            Join(PersonTeam,
                 User,
                 onColumn=personId,
                 otherColumn=User.id)
            .slice(personId, User.first_name, User.last_name, User.father_name)
            .select { PersonTeam.teamId eq teamId }.toList().map {
                TeammateResponseRemote(it[personId].value,
                                       it[User.first_name],
                                       it[User.last_name],
                                       it[User.father_name])
            }
        }
    }

    fun getMembersCount(teamId: Int): Int {
        return transaction {
            select { PersonTeam.teamId eq teamId }.count().toInt()
        }
    }

    fun delete(personId: Int) {
        transaction {
            val teamId: Int = select { PersonTeam.personId eq personId }.single()[teamId].value
            deleteWhere { PersonTeam.personId eq personId and (PersonTeam.teamId eq teamId) }
        }
    }
}
