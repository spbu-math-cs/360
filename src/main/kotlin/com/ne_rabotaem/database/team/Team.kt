package com.ne_rabotaem.database.team

import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.features.demo.TeamResponseRemote
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Team : IntIdTable("Team") {
    private val number = integer("number")
    private val name = varchar("name", 50)
    private val projectName = varchar("project_name", 50)
    private val teacherId = reference("teacher_id", User.id)

    fun insert(teamDTO: TeamDTO) {
        throw NotImplementedError()
    }

    fun fetch(teamId: Int): TeamDTO? {
        return try {
            throw NotImplementedError()
        } catch (e: Exception) {
            when (e) {
                is NoSuchElementException, is IllegalArgumentException -> null
                else -> {
                    throw e
                }
            }
        }
    }

    fun fetchAll(): List<TeamResponseRemote> {
        return transaction {
            Team.selectAll().toList().map {
                TeamResponseRemote(
                    it[Team.id].value,
                    it[number],
                    it[name],
                    it[projectName],
                    it[teacherId].value,
                )
            }
        }
    }
}
