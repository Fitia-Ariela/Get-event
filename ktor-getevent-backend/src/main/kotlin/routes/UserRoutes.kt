package routes

import controller.UserController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.userRoutes(controller: UserController) {
    route("/api/users") {
        authenticate("auth-jwt") {
            get { controller.list(call) }
            get("/stats") { controller.stats(call) }
            get("/{id}") { controller.get(call, call.parameters["id"]!!.toLong()) }
            post { controller.create(call) }
            put("/{id}") { controller.update(call, call.parameters["id"]!!.toLong()) }
            delete("/{id}") { controller.delete(call, call.parameters["id"]!!.toLong()) }
        }
    }
}
