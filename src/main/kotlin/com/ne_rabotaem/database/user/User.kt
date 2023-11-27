package com.ne_rabotaem.database.user

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
    val first_name = varchar("first_name", 24)
    val last_name = varchar("last_name", 24)
    val father_name = varchar("father_name", 24)
    private val login = varchar("login", 20)
    private val password = varchar("password", 20)
    val rank_ = customEnumeration("rank", "rank", { value -> rank.valueOf(value as String) }, { PGEnum("rank", it) })
    private val image_src = varchar("image_src", 100)


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

    fun addImage(userId: Int, imageSrc: String) {
        transaction {
            update({User.id eq userId}) {
                it[image_src] = imageSrc
            }
        }
    }

    fun fetch(login: String): UserDTO? {
        return getUserId(login)?.let { fetch(it) }
    }

    fun fetch(id: Int): UserDTO? {
        return transaction {
            val userModel = select { User.id eq id }.singleOrNull() ?: return@transaction null
            UserDTO(
                first_name = userModel[first_name],
                last_name = userModel[last_name],
                father_name = userModel[father_name],
                login = userModel[User.login],
                password = userModel[password],
                rank = userModel[rank_],
            )
        }
    }

    fun getUserId(login: String): Int? {
        return transaction {
            val userModel = select { User.login.eq(login) }.singleOrNull() ?: return@transaction null

            userModel[User.id].value
        }
    }

    fun updatePassword(userId: Int, password: String) {
        transaction {
            update ({ User.id eq userId }) {
                it[User.password] = password
            }
        }
    }

    fun checkSuperUser(userId: Int): Boolean {
        return transaction {
            select { User.id eq userId }.singleOrNull()?.get(rank_)
        } == rank.teacher
    }

    fun getImage(userId: Int): String? {
        return transaction {
            select { User.id eq userId }.singleOrNull()?.get(image_src)
        }
    }
}
