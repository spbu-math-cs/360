package com.ne_rabotaem.database.token

import com.ne_rabotaem.database.user.User
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object Token : Table("person") {
    private val id = Token.varchar("id", 50)
    private val login = varchar("login", 20)
    private val token = varchar("token", 128)

    fun insert(tokenDTO: TokenDTO) {
        transaction {
            User.insert {
                it[Token.id] = tokenDTO.rowId
                it[login] = tokenDTO.login
                it[token] = tokenDTO.token
            }
        }
    }
}
