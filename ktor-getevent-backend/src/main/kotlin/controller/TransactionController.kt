package controller

import dto.TransactionRequest
import model.Role
import security.UserPrincipal
import security.requireRole
import service.TransactionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class TransactionController(private val transactionService: TransactionService) {

    suspend fun pay(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>().requireRole(Role.STUDENT)
        val request = call.receive<TransactionRequest>()
        call.respond(HttpStatusCode.Created, transactionService.pay(principal.userId, request))
    }

    suspend fun getByReservation(call: ApplicationCall, reservationId: Long) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Non authentifié"))
        val transaction = transactionService.findByReservation(reservationId)
            ?: return call.respond(HttpStatusCode.NotFound, mapOf("error" to "Transaction introuvable"))
        call.respond(transaction)
    }
}
