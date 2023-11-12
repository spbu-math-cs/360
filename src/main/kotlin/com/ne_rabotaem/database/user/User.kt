package com.ne_rabotaem.database.user

import org.jetbrains.exposed.dao.id.IntIdTable
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

object User : IntIdTable("Person") {
    val first_name_max_length = 24
    val last_name_max_length = 24
    val father_name_max_length = 24
    val login_max_length = 20
    val password_max_length = 20

    private val first_name = varchar("first_name", first_name_max_length)
    private val last_name = varchar("last_name", last_name_max_length)
    private val father_name = varchar("father_name", father_name_max_length)
    private val login = varchar("login", login_max_length)
    private val password = varchar("password", password_max_length)
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
        return getUserId(login)?.let { fetch(it) }
    }

    fun fetch(id: Int): UserDTO? {
        return try {
            transaction {
                val userModel = select { User.id eq id }.single()
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

    fun getUserId(login: String): Int? {
        return transaction {
            val userModel = select { User.login.eq(login) }.firstOrNull() ?: return@transaction null

            userModel[User.id].value
        }
    }
}
