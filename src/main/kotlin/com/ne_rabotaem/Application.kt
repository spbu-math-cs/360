package com.ne_rabotaem

import com.ne_rabotaem.database.DatabaseFactory.configureDataBaseInitialization
import com.ne_rabotaem.features.demo.configureDemoRouting
import com.ne_rabotaem.features.demo.statistics.configureStatisticsRouting
import com.ne_rabotaem.features.home.configureAboutRouting
import com.ne_rabotaem.features.home.configureHomeRouting
import com.ne_rabotaem.features.login.configureLoginRouting
import com.ne_rabotaem.features.profile.configureProfileRouting
import com.ne_rabotaem.features.register.configureRegisterRouting
import com.ne_rabotaem.features.vote.configureVoteRouting
import com.ne_rabotaem.plugins.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDataBaseInitialization()

    configureRouting()
    configureHomeRouting()
    configureLoginRouting()
    configureRegisterRouting()
    configureDemoRouting()
    configureStatisticsRouting()
    configureVoteRouting()
    configureAboutRouting()
    configureProfileRouting()
}
