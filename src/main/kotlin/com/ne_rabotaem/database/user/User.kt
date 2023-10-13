package com.ne_rabotaem.database.user

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PGobject
import java.lang.IllegalArgumentException

enum class rank {
    second_grade,
    fourth_grade,
    teacher,
    god,
}

class PGEnum<T : Enum<T>>(enumTypeName: String, enumValue: T?) : PGobject() {
    init {
        value = enumValue?.name
        type = enumTypeName
    }
}

object User : Table("Person") {
    private val first_name = varchar("first_name", 24)
    private val last_name = varchar("last_name", 24)
    private val father_name = varchar("father_name", 24)
    private val login = varchar("login", 20)
    private val password = varchar("password", 20)
    private val rank_ = customEnumeration("rank", "rank", { value -> rank.valueOf(value as String) }, { PGEnum("rank", it) })


    fun insert(userDTO: UserDTO) {
        transaction {
            insert {
                it[first_name] = userDTO.first_name
                it[last_name] = userDTO.last_name
                it[father_name] = userDTO.father_name
                it[login] = userDTO.login
                it[password] = userDTO.password
                it[rank_] = userDTO.rank
            }
        }
    }

    fun fetch(login: String): UserDTO? {
        return try {
            transaction {
                val userModel = select { User.login.eq(login) }.single()
                UserDTO(
                    first_name = userModel[first_name],
                    last_name = userModel[last_name],
                    father_name = userModel[father_name],
                    login = userModel[User.login],
                    password = userModel[password],
                    rank = userModel[rank_],
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
