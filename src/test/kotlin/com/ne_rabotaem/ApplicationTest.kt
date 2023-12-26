package com.ne_rabotaem

import com.ne_rabotaem.database.event.Event
import com.ne_rabotaem.database.grade.DemoGrade
import com.ne_rabotaem.database.team.Team
import com.ne_rabotaem.database.token.Token
import com.ne_rabotaem.database.user.User
import com.ne_rabotaem.database.user.UserDTO
import com.ne_rabotaem.database.user.rank
import com.ne_rabotaem.features.demo.configureDemoRouting
import com.ne_rabotaem.features.home.configureAboutRouting
import com.ne_rabotaem.features.home.configureHomeRouting
import com.ne_rabotaem.features.login.LoginReceiveRemote
import com.ne_rabotaem.features.login.LoginResponseRemote
import com.ne_rabotaem.features.login.configureLoginRouting
import com.ne_rabotaem.features.register.configureRegisterRouting
import com.ne_rabotaem.plugins.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class ApplicationTest {
    val numberOfTestUsers = 10L
    val loginToToken = HashMap<String, String>()
    val loginToUser = HashMap<String, UserDTO>()

//    private fun connectToTestDB() {
//        Database.connect(
//            "jdbc:postgresql://localhost:5432/DEV_ne_rabotaem",
//            driver = "org.postgresql.Driver",
//            user = "postgres",
//            password = "dubchuk",
//        )
//    }
//
//    private fun ApplicationTestBuilder.setApplication() {
//        application {
//            connectToTestDB()
//            configureSerialization()
//            configureRouting()
//            configureHomeRouting()
//            configureLoginRouting()
//            configureRegisterRouting()
//            configureDemoRouting()
//            configureAboutRouting()
//        }
//    }
//
//    private fun getRandomString(length: Int): String {
//        val allowedChars = (Char.MIN_VALUE..Char.MAX_VALUE)
//        return (1..length)
//            .map { allowedChars.random() }
//            .joinToString("")
//    }
//
//    @BeforeTest
//    fun generateTestData() {
//        while (loginToUser.size < numberOfTestUsers) {
//            var login = getRandomString((1..User.login_max_length).random())
//            while (login in loginToUser.keys) {
//                login = getRandomString((1..User.login_max_length).random())
//            }
//            val first_name = getRandomString((1..User.first_name_max_length).random())
//            val last_name = getRandomString((1..User.last_name_max_length).random())
//            val father_name = getRandomString((1..User.father_name_max_length).random())
//            val password = getRandomString((1..User.password_max_length).random())
//            val rank_ = rank.values().random()
//            val user = UserDTO(first_name, last_name, father_name, login, password, rank_)
//            loginToUser[login] = user
//        }
//    }
//
//    @AfterTest
//    fun clean() {
//        loginToToken.clear()
//        loginToUser.clear()
//
//        connectToTestDB()
//        User.run {
//            transaction {
//                deleteAll()
//            }
//        }
//        Token.run {
//            transaction {
//                deleteAll()
//            }
//        }
//        Team.run {
//            transaction {
//                deleteAll()
//            }
//        }
//        DemoGrade.run {
//            transaction {
//                deleteAll()
//            }
//        }
//        Event.run {
//            transaction {
//                deleteAll()
//            }
//        }
//    }
//
//    @Test
//    fun basicRegistrationAndLoginTest() = testApplication {
//        setApplication()
//
//        // check if registration is correct
//        for (user in loginToUser.values) {
//            val response = client.post("/register") {
//                contentType(ContentType.Application.Json)
//                setBody(Json.encodeToString(user))
//            }
//            assertEquals(HttpStatusCode.OK, response.status)
//        }
//        var numberOfQueries: Long = 0
//
//        User.run {
//            transaction {
//                numberOfQueries = selectAll().count()
//            }
//        }
//        assertEquals(numberOfTestUsers, numberOfQueries)
//
//        // check if double registration is NOT allowed
//        for (user in loginToUser.values) {
//            val response = client.post("/register") {
//                contentType(ContentType.Application.Json)
//                setBody(Json.encodeToString(user))
//            }
//            assertEquals(HttpStatusCode.Conflict, response.status)
//        }
//        User.run {
//            transaction {
//                numberOfQueries = selectAll().count()
//            }
//        }
//        assertEquals(numberOfTestUsers, numberOfQueries)
//
//        // check if login handles correctly
//        for (user in loginToUser.values) {
//            val response = client.post("/login") {
//                contentType(ContentType.Application.Json)
//                setBody(Json.encodeToString(LoginReceiveRemote(user.login, user.password)))
//            }
//            assertEquals(HttpStatusCode.OK, response.status)
//
//            var tokenFromDB = ""
//            Token.run {
//                transaction {
//                    tokenFromDB = select { login eq user.login }.single()[token]
//                }
//            }
//            val tokenFromResponse = Json.decodeFromString<LoginResponseRemote>(response.body()).token
//            assertEquals(tokenFromResponse, tokenFromDB)
//
//            loginToToken[user.login] = tokenFromDB
//        }
//
//        Token.run {
//            transaction {
//                numberOfQueries = selectAll().count()
//            }
//        }
//        assertEquals(numberOfTestUsers, numberOfQueries)
//
//        // check if double login is allowed
//        for (user in loginToUser.values) {
//            val response = client.post("/login") {
//                contentType(ContentType.Application.Json)
//                setBody(Json.encodeToString(LoginReceiveRemote(user.login, user.password)))
//            }
//            assertEquals(HttpStatusCode.OK, response.status)
//
//            var tokenFromDB = ""
//            Token.run {
//                transaction {
//                    tokenFromDB = select { login eq user.login }.last()[token]
//                }
//            }
//            val tokenFromResponse = Json.decodeFromString<LoginResponseRemote>(response.body()).token
//            assertEquals(tokenFromResponse, tokenFromDB)
//        }
//
//        Token.run {
//            transaction {
//                numberOfQueries = selectAll().count()
//            }
//        }
//        assertEquals(2*numberOfTestUsers, numberOfQueries)
//
//        // check if login with wrong password is NOT allowed
//        if (loginToUser.isNotEmpty()) {
//            val login = loginToUser.keys.first()
//            var wrongPassword = getRandomString((1..User.password_max_length).random())
//            while (wrongPassword == loginToUser[login]!!.password) {
//                wrongPassword = getRandomString((1..User.password_max_length).random())
//            }
//            val response = client.post("/login") {
//                contentType(ContentType.Application.Json)
//                setBody(Json.encodeToString(LoginReceiveRemote(login, wrongPassword)))
//            }
//            assertEquals(HttpStatusCode.BadRequest, response.status)
//        }
//    }
//
//    @Test
//    fun nonExistingLoginTest() = testApplication {
//        setApplication()
//
//        var login = getRandomString((1..User.login_max_length).random())
//        while (login in loginToUser.keys) {
//            login = getRandomString((1..User.login_max_length).random())
//        }
//        val password = getRandomString((1..User.password_max_length).random())
//        val response = client.post("/login") {
//            contentType(ContentType.Application.Json)
//            setBody(Json.encodeToString(LoginReceiveRemote(login, password)))
//        }
//        assertEquals(HttpStatusCode.Conflict, response.status)
//    }
//
//    @Test
//    fun emptyLoginInRegisterTest() {
//        TODO()
//    }
//
//    @Test
//    fun emptyFirstNameInRegisterTest() {
//        TODO()
//    }
//
//    @Test
//    fun emptyLastNameInRegisterTest() {
//        TODO()
//    }
//
//    @Test
//    fun emptyPasswordInRegisterTest() {
//        TODO()
//    }
}
