package com.ne_rabotaem.features.demo

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.utils.TokenCheck
import com.ne_rabotaem.utils.UserCheck
import com.ne_rabotaem.utils.UserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DemoController(val call: ApplicationCall) {
    private val isUserValid get() = UserCheck.isUserExists(call)
    private val userId: Int

    init {
        runBlocking {
            if (!isUserValid) {
                call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
                return@runBlocking
            }
        }

        userId = UserId.getId(call)!!
    }

    suspend fun getPage() {
        call.respond(MustacheContent("demo.html", mapOf<String, String>()))
    }

    suspend fun getStatisticsPage() {
        call.respond(MustacheContent("statistics.html", mapOf<String, String>()))
    }

    suspend fun getDemos() {
        call.respond(Json.encodeToString(Event.fetchAll()))
    }
}
