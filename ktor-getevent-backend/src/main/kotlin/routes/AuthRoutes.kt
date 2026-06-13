package routes

import controller.AuthController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.authRoutes(controller: AuthController) {
    route("/api/auth") {
        post("/login") { controller.login(call) }
        post("/register") { controller.register(call) }
        authenticate("auth-jwt") {
            get("/me") { controller.me(call) }
        }
    }
}
