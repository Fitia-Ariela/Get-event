package api

import TestFixtures
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventApiTest {

    @Test
    fun listEventsIsPublic() = ApiTestClient.runApiTest {
        val response = client.get("/api/events")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun adminCrudEventAndLocation() = ApiTestClient.runApiTest(seedAdmin = true) {
        val token = login("admin@getevent.local", "admin123")

        val location = client.post("/api/locations") {
            authHeader(token)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"nom":"Salle B","longitude":47.5,"latitude":-18.9,"capacite":100}
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.Created, location.status)
        val lieuId = Regex(""""id"\s*:\s*(\d+)""").find(location.bodyAsText())!!.groupValues[1].toLong()

        val event = client.post("/api/events") {
            authHeader(token)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "nomEvenement":"Workshop",
                  "dateEvenement":"2026-07-01T09:00:00Z",
                  "lieuId":$lieuId,
                  "description":"Atelier",
                  "estPrive":false,
                  "tarif":0
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.Created, event.status)
        val eventId = Regex(""""idEvenement"\s*:\s*(\d+)""").find(event.bodyAsText())!!.groupValues[1].toLong()

        val get = client.get("/api/events/$eventId")
        assertEquals(HttpStatusCode.OK, get.status)
        assertTrue(get.bodyAsText().contains("Workshop"))

        val places = client.get("/api/events/$eventId/places")
        assertEquals(HttpStatusCode.OK, places.status)

        val delete = client.delete("/api/events/$eventId") { authHeader(token) }
        assertEquals(HttpStatusCode.NoContent, delete.status)
    }

    @Test
    fun studentCannotCreateEvent() = ApiTestClient.runApiTest {
        val email = TestFixtures.uniqueEmail()
        registerStudent(email)
        val token = login(email, "password123")

        val response = client.post("/api/events") {
            authHeader(token)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "nomEvenement":"Hack",
                  "dateEvenement":"2026-07-01T09:00:00Z",
                  "lieuId":1,
                  "description":"x",
                  "estPrive":false
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
