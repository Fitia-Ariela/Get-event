package controller

import dto.ReservationRequest
import model.Role
import security.UserPrincipal
import security.requireRole
import service.ReservationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class ReservationController(private val reservationService: ReservationService) {

    suspend fun listMine(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Non authentifié"))
        call.respond(reservationService.listByUser(principal.userId))
    }

    suspend fun listAll(call: ApplicationCall) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN, Role.BOARD_MEMBER)
        call.respond(reservationService.listAll())
    }

    suspend fun get(call: ApplicationCall, id: Long) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Non authentifié"))
        val isStaff = principal.role == Role.ADMIN || principal.role == Role.BOARD_MEMBER
        call.respond(reservationService.getById(id, principal.userId, isStaff))
    }

    suspend fun create(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>().requireRole(Role.STUDENT)
        val request = call.receive<ReservationRequest>()
        call.respond(HttpStatusCode.Created, reservationService.create(principal.userId, request))
    }

    suspend fun cancel(call: ApplicationCall, id: Long) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Non authentifié"))
        val isAdmin = principal.role == Role.ADMIN
        call.respond(reservationService.cancel(id, principal.userId, isAdmin))
    }
}
