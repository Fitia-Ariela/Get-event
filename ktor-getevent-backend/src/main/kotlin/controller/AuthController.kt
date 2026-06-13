package controller

import dto.AuthResponse
import dto.LoginRequest
import dto.RegisterRequest
import dto.toResponse
import model.Role
import security.UserPrincipal
import service.AuthService
import service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AuthController(
    private val authService: AuthService,
    private val userService: UserService
) {

    suspend fun login(call: ApplicationCall) {
        val request = call.receive<LoginRequest>()
        val response = authService.login(request)
        call.respond(HttpStatusCode.OK, response)
    }

    suspend fun register(call: ApplicationCall) {
        val request = call.receive<RegisterRequest>()
        val principal = call.principal<UserPrincipal>()
        val response = authService.register(request, principal?.role)
        call.respond(HttpStatusCode.Created, response)
    }

    suspend fun me(call: ApplicationCall) {
        val principal = call.principal<UserPrincipal>()
            ?: return call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Non authentifié"))
        val user = userService.findById(principal.userId)
            ?: return call.respond(HttpStatusCode.NotFound, mapOf("error" to "Utilisateur introuvable"))
        call.respond(user.toResponse())
    }
}
