package com.ne_rabotaem.features.demo

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.grade.Demo_grade
import com.ne_rabotaem.database.grade.GradeDTO
import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.utils.TokenCheck
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DemoController(val call: ApplicationCall) {
    suspend fun getPage() {
        if (!TokenCheck.isTokenValid(call)) {
            call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
            return
        }
        call.respond(MustacheContent("demo.html", mapOf<String, String>()))
    }

    suspend fun getDemos() {
        if (!TokenCheck.isTokenValid(call)) {
            call.respond(HttpStatusCode.Unauthorized, "Wrong token!")
            return
        }

        call.respond(Json.encodeToString(Event.fetchAll()))
    }
}
