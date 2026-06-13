package security

import model.Role
import util.JwtConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier())
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asLong() ?: return@validate null
                val email = credential.payload.getClaim("email").asString() ?: return@validate null
                val roleName = credential.payload.getClaim("role").asString() ?: return@validate null
                val role = runCatching { Role.valueOf(roleName) }.getOrNull() ?: return@validate null
                UserPrincipal(userId, email, role)
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token invalide ou expiré"))
            }
        }
    }
}

fun UserPrincipal?.requireRole(vararg roles: Role): UserPrincipal {
    val principal = this ?: throw IllegalAccessException("Authentification requise")
    if (principal.role !in roles) {
        throw IllegalAccessException("Accès refusé pour ce rôle")
    }
    return principal
}
