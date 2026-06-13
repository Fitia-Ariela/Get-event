package api

import TestFixtures
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthApiTest {

    @Test
    fun registerAndLoginStudent() = ApiTestClient.runApiTest {
        val email = TestFixtures.uniqueEmail()
        val register = registerStudent(email)
        assertEquals(HttpStatusCode.Created, register.status)

        val token = login(email, "password123")
        assertTrue(token.isNotBlank())

        val me = client.get("/api/auth/me") { authHeader(token) }
        assertEquals(HttpStatusCode.OK, me.status)
        assertTrue(me.bodyAsText().contains(email))
    }

    @Test
    fun loginWithInvalidCredentialsReturnsBadRequest() = ApiTestClient.runApiTest {
        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"nobody@test.local","password":"wrong"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
