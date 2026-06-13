package controller

import model.Role
import security.UserPrincipal
import security.requireRole
import service.ReservationService
import service.TicketService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

class TicketController(
    private val ticketService: TicketService,
    private val reservationService: ReservationService
) {

    suspend fun getByReservation(call: ApplicationCall, reservationId: Long) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Non authentifié"))
        val isStaff = principal.role == Role.ADMIN || principal.role == Role.BOARD_MEMBER
        reservationService.getById(reservationId, principal.userId, isStaff)
        val ticket = ticketService.findByReservation(reservationId)
            ?: return call.respond(HttpStatusCode.NotFound, mapOf("error" to "Ticket non généré"))
        call.respond(ticket)
    }

    suspend fun markUsed(call: ApplicationCall, id: Long) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN, Role.BOARD_MEMBER)
        call.respond(ticketService.markAsUsed(id))
    }
}
