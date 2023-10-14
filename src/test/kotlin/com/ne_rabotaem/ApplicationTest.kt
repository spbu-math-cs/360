package com.ne_rabotaem

import com.ne_rabotaem.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testMain() = testApplication {
        application {
            configureRouting()
        }
        client.get("/test").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Test passed!", bodyAsText())
        }
    }
}
