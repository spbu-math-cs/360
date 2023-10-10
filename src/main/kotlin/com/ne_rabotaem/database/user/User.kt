package com.ne_rabotaem.database.user

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

//enum class rank(val str: String) {
//    second_grade("second_grade"),
//    fourth_grade("fourth_grade"),
//    teacher("teacher"),
//    god("god"),
//}
object User : Table("person") {
    private val first_name: Column<String> = varchar("first_name", 24)
    private val last_name: Column<String> = varchar("last_name", 24)
    private val father_name: Column<String> = varchar("father_name", 24)
    private val login: Column<String> = varchar("login", 20)
    private val password: Column<String> = varchar("password", 20)
    private val rank: Column<String> = varchar("rank", 16)

    fun insert(userDTO: UserDTO) {
        transaction {
            addLogger(StdOutSqlLogger)
            User.insert {
                it[first_name] = userDTO.first_name
                it[last_name] = userDTO.last_name
                it[father_name] = userDTO.father_name
                it[login] = userDTO.login
                it[password] = userDTO.password
                it[rank] = userDTO.rank
            }
        }
    }

    fun fetchUser(login: String): UserDTO? {
        return try {
            val userModel = select { User.login.eq(login) }.single()
            UserDTO(
                first_name = userModel[first_name],
                last_name = userModel[last_name],
                father_name = userModel[father_name],
                login = userModel[User.login],
                password = userModel[password],
                rank = userModel[rank],
            )
        } catch (e: Exception) {
            null
        }
    }
}
