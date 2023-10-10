package com.ne_rabotaem.database.token

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object Token : Table("Token") {
    private val login = varchar("login", 20)
    private val token = varchar("token", 128)

    fun insert(tokenDTO: TokenDTO) {
        transaction {
            insert {
                it[login] = tokenDTO.login
                it[token] = tokenDTO.token
            }
        }
    }
}
