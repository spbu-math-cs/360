package com.ne_rabotaem.database.person_team

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.user.User
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Invite : IntIdTable("Invite") {
    private val fromWhom = reference("from_whom", User.id)
    private val toWhom = reference("to_whom", User.id)
    private val teamId = reference("team_id", Team.id)

    fun insert(inviteDTO: InviteDTO) {
        transaction {
            insert {
                it[fromWhom] = inviteDTO.fromWhom
                it[toWhom] = inviteDTO.toWhom
                it[teamId] = inviteDTO.teamId
            }
        }
    }

    fun getInvites(userId: Int): List<InviteIdDTO> {
        return transaction {
            select { toWhom eq userId }.map {
                InviteIdDTO(
                    id = it[Invite.id].value,
                    teamId = it[teamId].value,
                    toWhom = it[toWhom].value,
                    fromWhom = it[fromWhom].value
                )
            }
        }
    }

    fun delete(personId: Int, newTeamId: Int) {
        transaction {
            deleteWhere { toWhom eq personId and (teamId eq newTeamId) }
        }
    }
}