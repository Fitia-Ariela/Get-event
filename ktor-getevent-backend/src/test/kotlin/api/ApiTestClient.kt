package api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.*
import module
import java.io.File

object ApiTestClient {

    fun runApiTest(
        seedAdmin: Boolean = false,
        block: suspend ApplicationTestBuilder.() -> Unit
    ) = testApplication {
        val storeFile = File.createTempFile("api-test-", ".json")
        storeFile.deleteOnExit()
        environment {
            config = MapApplicationConfig(
                "jwt.secret" to "test-secret-key-for-unit-tests-only!!",
                "jwt.issuer" to "getevent",
                "jwt.audience" to "getevent-api",
                "jwt.realm" to "GetEvent",
                "data.store.path" to storeFile.absolutePath,
                "data.seedAdmin" to seedAdmin.toString()
            )
        }
        application { module() }
        block()
    }
}

suspend fun ApplicationTestBuilder.login(email: String, password: String): String {
    val response = client.post("/api/auth/login") {
        contentType(ContentType.Application.Json)
        setBody("""{"email":"$email","password":"$password"}""")
    }
    val body = response.bodyAsText()
    require(response.status == HttpStatusCode.OK) { "Login failed: $body" }
    return Regex(""""token"\s*:\s*"([^"]+)"""").find(body)?.groupValues?.get(1)
        ?: error("Token not found in: $body")
}

suspend fun ApplicationTestBuilder.registerStudent(email: String): HttpResponse =
    client.post("/api/auth/register") {
        contentType(ContentType.Application.Json)
        setBody(
            """
            {
              "numeroInscription": "ETU001",
              "nom": "Test Student",
              "niveau": "L3",
              "parcours": "Telecom",
              "numeroTel": 340000001,
              "email": "$email",
              "nomFacebook": "fb",
              "password": "password123",
              "role": "STUDENT"
            }
            """.trimIndent()
        )
    }

fun HttpRequestBuilder.authHeader(token: String) {
    headers { append(HttpHeaders.Authorization, "Bearer $token") }
}
