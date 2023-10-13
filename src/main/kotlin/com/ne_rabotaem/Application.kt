package com.ne_rabotaem

import com.ne_rabotaem.features.demo.configureDemoRouting
import com.ne_rabotaem.features.login.configureLoginRouting
import com.ne_rabotaem.features.register.configureRegisterRouting
import com.ne_rabotaem.plugins.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database

fun main() {
    Database.connect("jdbc:postgresql://localhost:5432/ne_rabotaem", driver = "org.postgresql.Driver",
        user="postgres", password = "dubchuk")
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureLoginRouting()
    configureRegisterRouting()
    configureDemoRouting()
}
