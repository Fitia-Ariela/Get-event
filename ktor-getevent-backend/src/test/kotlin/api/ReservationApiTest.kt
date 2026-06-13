package api

import TestFixtures
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReservationApiTest {

    @Test
    fun fullPublicEventFlow() = ApiTestClient.runApiTest(seedAdmin = true) {
        val adminToken = login("admin@getevent.local", "admin123")
        val studentEmail = TestFixtures.uniqueEmail()
        registerStudent(studentEmail)
        val studentToken = login(studentEmail, "password123")

        val location = client.post("/api/locations") {
            authHeader(adminToken)
            contentType(ContentType.Application.Json)
            setBody("""{"nom":"Hall","longitude":1.0,"latitude":2.0,"capacite":10}""")
        }
        val lieuId = Regex(""""id"\s*:\s*(\d+)""").find(location.bodyAsText())!!.groupValues[1]

        val event = client.post("/api/events") {
            authHeader(adminToken)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "nomEvenement":"Meetup",
                  "dateEvenement":"2026-08-01T10:00:00Z",
                  "lieuId":$lieuId,
                  "description":"Public",
                  "estPrive":false
                }
                """.trimIndent()
            )
        }
        val eventId = Regex(""""idEvenement"\s*:\s*(\d+)""").find(event.bodyAsText())!!.groupValues[1]

        val reservation = client.post("/api/reservations") {
            authHeader(studentToken)
            contentType(ContentType.Application.Json)
            setBody("""{"evenementId":$eventId}""")
        }
        assertEquals(HttpStatusCode.Created, reservation.status)
        val reservationId = Regex(""""idReservation"\s*:\s*(\d+)""")
            .find(reservation.bodyAsText())!!.groupValues[1]

        val mine = client.get("/api/reservations/me") { authHeader(studentToken) }
        assertEquals(HttpStatusCode.OK, mine.status)

        val ticket = client.get("/api/tickets/reservation/$reservationId") { authHeader(studentToken) }
        assertEquals(HttpStatusCode.OK, ticket.status)
        assertTrue(ticket.bodyAsText().contains("TICKET-"))
    }

    @Test
    fun fullPrivateEventFlowWithPayment() = ApiTestClient.runApiTest(seedAdmin = true) {
        val adminToken = login("admin@getevent.local", "admin123")
        val studentEmail = TestFixtures.uniqueEmail()
        registerStudent(studentEmail)
        val studentToken = login(studentEmail, "password123")

        val location = client.post("/api/locations") {
            authHeader(adminToken)
            contentType(ContentType.Application.Json)
            setBody("""{"nom":"VIP","longitude":1.0,"latitude":2.0,"capacite":5}""")
        }
        val lieuId = Regex(""""id"\s*:\s*(\d+)""").find(location.bodyAsText())!!.groupValues[1]

        val event = client.post("/api/events") {
            authHeader(adminToken)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "nomEvenement":"Gala",
                  "dateEvenement":"2026-09-01T20:00:00Z",
                  "lieuId":$lieuId,
                  "description":"Privé",
                  "estPrive":true,
                  "tarif":2500
                }
                """.trimIndent()
            )
        }
        val eventId = Regex(""""idEvenement"\s*:\s*(\d+)""").find(event.bodyAsText())!!.groupValues[1]

        val reservation = client.post("/api/reservations") {
            authHeader(studentToken)
            contentType(ContentType.Application.Json)
            setBody("""{"evenementId":$eventId}""")
        }
        val reservationId = Regex(""""idReservation"\s*:\s*(\d+)""")
            .find(reservation.bodyAsText())!!.groupValues[1]

        val pay = client.post("/api/transactions/pay") {
            authHeader(studentToken)
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "reservationId":$reservationId,
                  "modePaiement":"MVola",
                  "referencePaiement":"PAY-001"
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.Created, pay.status)

        val ticket = client.get("/api/tickets/reservation/$reservationId") { authHeader(studentToken) }
        assertEquals(HttpStatusCode.OK, ticket.status)
    }
}
