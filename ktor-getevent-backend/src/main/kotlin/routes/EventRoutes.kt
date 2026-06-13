package routes

import controller.EventController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.eventRoutes(controller: EventController) {
    route("/api/events") {
        get { controller.list(call) }
        get("/{id}") { controller.get(call, call.parameters["id"]!!.toLong()) }
        get("/{id}/places") { controller.availablePlaces(call, call.parameters["id"]!!.toLong()) }
        authenticate("auth-jwt") {
            post { controller.create(call) }
            put("/{id}") { controller.update(call, call.parameters["id"]!!.toLong()) }
            delete("/{id}") { controller.delete(call, call.parameters["id"]!!.toLong()) }
        }
    }
    route("/api/locations") {
        get { controller.listLocations(call) }
        authenticate("auth-jwt") {
            post { controller.createLocation(call) }
            put("/{id}") { controller.updateLocation(call, call.parameters["id"]!!.toLong()) }
            delete("/{id}") { controller.deleteLocation(call, call.parameters["id"]!!.toLong()) }
        }
    }
}
