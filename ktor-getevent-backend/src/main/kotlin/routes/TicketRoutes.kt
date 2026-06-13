package routes

import controller.TicketController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.ticketRoutes(controller: TicketController) {
    route("/api/tickets") {
        authenticate("auth-jwt") {
            get("/reservation/{reservationId}") {
                controller.getByReservation(call, call.parameters["reservationId"]!!.toLong())
            }
            put("/{id}/use") { controller.markUsed(call, call.parameters["id"]!!.toLong()) }
        }
    }
}
