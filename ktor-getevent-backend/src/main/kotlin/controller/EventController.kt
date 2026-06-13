package controller

import dto.EventRequest
import dto.LocationRequest
import model.Role
import security.UserPrincipal
import security.requireRole
import service.EventService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class EventController(private val eventService: EventService) {

    suspend fun list(call: ApplicationCall) {
        val events=eventService.listEvents();
        println(events)
        call.respond(eventService.listEvents())
    }

    suspend fun get(call: ApplicationCall, id: Long) {
        call.respond(eventService.getEvent(id))
    }

    suspend fun availablePlaces(call: ApplicationCall, id: Long) {
        call.respond(mapOf("placesDisponibles" to eventService.availablePlaces(id)))
    }

    suspend fun create(call: ApplicationCall) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN)
        
        val request = call.receive<EventRequest>()
        
        call.respond(HttpStatusCode.Created, eventService.createEvent(request))
    }

    suspend fun update(call: ApplicationCall, id: Long) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN)
        val request = call.receive<EventRequest>()
        call.respond(eventService.updateEvent(id, request))
    }

    suspend fun delete(call: ApplicationCall, id: Long) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN)
        eventService.deleteEvent(id)
        call.respond(HttpStatusCode.NoContent)
    }

    suspend fun listLocations(call: ApplicationCall) {
        call.respond(eventService.listLocations())
    }

    suspend fun createLocation(call: ApplicationCall) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN)
        val request = call.receive<LocationRequest>()
        call.respond(HttpStatusCode.Created, eventService.createLocation(request))
    }

    suspend fun updateLocation(call: ApplicationCall, id: Long) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN)
        val request = call.receive<LocationRequest>()
        call.respond(eventService.updateLocation(id, request))
    }

    suspend fun deleteLocation(call: ApplicationCall, id: Long) {
        call.principal<UserPrincipal>().requireRole(Role.ADMIN)
        eventService.deleteLocation(id)
        call.respond(HttpStatusCode.NoContent)
    }
}
