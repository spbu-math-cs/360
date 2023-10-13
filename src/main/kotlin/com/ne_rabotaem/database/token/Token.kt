package com.ne_rabotaem.database.token

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IllegalArgumentException

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

    fun fetch(token: String): TokenDTO? {
        return try {
            transaction {
                val tokenModel = select { Token.token.eq(token) }.single()
                TokenDTO(
                    login = tokenModel[login],
                    token = tokenModel[Token.token]
                )
            }
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
