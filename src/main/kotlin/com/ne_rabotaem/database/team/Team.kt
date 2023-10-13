package com.ne_rabotaem.database.team

import com.ne_rabotaem.database.user.User
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
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
            when(e) {
                is NoSuchElementException, is IllegalArgumentException -> null
                else -> {
                    throw e
                }
            }
        }
    }
}