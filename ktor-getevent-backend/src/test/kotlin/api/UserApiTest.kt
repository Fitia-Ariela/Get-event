package api

import TestFixtures
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserApiTest {

    @Test
    fun boardMemberCanListUsersAndStats() = ApiTestClient.runApiTest(seedAdmin = true) {
        val adminToken = login("admin@getevent.local", "admin123")
        val memberEmail = TestFixtures.uniqueEmail()
        client.post("/api/users") {
            authHeader(adminToken)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "numeroInscription": "BRD001",
                  "nom": "Membre Bureau",
                  "niveau": "L3",
                  "parcours": "Telecom",
                  "numeroTel": 340000002,
                  "email": "$memberEmail",
                  "nomFacebook": "fb",
                  "password": "password123",
                  "role": "BOARD_MEMBER"
                }
                """.trimIndent()
            )
        }
        val memberToken = login(memberEmail, "password123")

        val list = client.get("/api/users") { authHeader(memberToken) }
        assertEquals(HttpStatusCode.OK, list.status)

        val stats = client.get("/api/users/stats") { authHeader(memberToken) }
        assertEquals(HttpStatusCode.OK, stats.status)
        assertTrue(stats.bodyAsText().contains("total"))
    }

    @Test
    fun studentCannotListUsers() = ApiTestClient.runApiTest {
        val email = TestFixtures.uniqueEmail()
        registerStudent(email)
        val token = login(email, "password123")

        val response = client.get("/api/users") { authHeader(token) }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
