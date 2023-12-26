package com.ne_rabotaem.database

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig

object DatabaseFactory {
    private const val dbUrl = "jdbc:postgresql://localhost:5432/ne_rabotaem"
    private val dbUser = System.getenv("DB_POSTGRES_USER")
    private val dbPassword = System.getenv("DB_PASSWORD")

    fun Application.configureDataBaseInitialization() {
        Database.connect(
            url = dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword,
        )
    }
}