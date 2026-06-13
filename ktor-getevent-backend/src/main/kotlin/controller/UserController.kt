package controller

import dto.RegisterRequest
import dto.toResponse
import model.Role
import security.UserPrincipal
import security.requireRole
import service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class UserController(private val userService: UserService) {

    suspend fun list(call: ApplicationCall) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN, Role.BOARD_MEMBER)
        val users = userService.findAll().map { it.toResponse() }
        call.respond(users)
    }

    suspend fun stats(call: ApplicationCall) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN, Role.BOARD_MEMBER)
        call.respond(userService.stats())
    }

    suspend fun get(call: ApplicationCall, id: Long) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN, Role.BOARD_MEMBER)
        val user = userService.findById(id)
            ?: return call.respond(HttpStatusCode.NotFound, mapOf("error" to "Utilisateur introuvable"))
        call.respond(user.toResponse())
    }

    suspend fun create(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>().requireRole(Role.ADMIN)
        val request = call.receive<RegisterRequest>()
        val user = userService.create(request, principal.role)
        call.respond(HttpStatusCode.Created, user.toResponse())
    }

    suspend fun update(call: ApplicationCall, id: Long) {
        val principal = call.principal<UserPrincipal>().requireRole(Role.ADMIN)
        val request = call.receive<RegisterRequest>()
        val user = userService.update(id, request, principal.role)
        call.respond(user.toResponse())
    }

    suspend fun delete(call: ApplicationCall, id: Long) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN)
        userService.delete(id)
        call.respond(HttpStatusCode.NoContent)
    }
}
