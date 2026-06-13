package com.getevent.mobile.app.repository

import com.getevent.mobile.app.api.AuthResponse
import com.getevent.mobile.app.api.EventRequest
import com.getevent.mobile.app.api.LocationRequest
import com.getevent.mobile.app.api.LoginRequest
import com.getevent.mobile.app.api.RegisterRequest
import com.getevent.mobile.app.api.ReservationRequest
import com.getevent.mobile.app.api.UserStatsResponse
import com.getevent.mobile.app.model.Event
import com.getevent.mobile.app.model.Location
import com.getevent.mobile.app.model.Reservation
import com.getevent.mobile.app.model.Ticket
import com.getevent.mobile.app.model.User
import com.getevent.mobile.app.model.Role
import com.getevent.mobile.app.utils.NetworkModule

class GetEventRepository {
    suspend fun login(email: String, password: String): AuthResponse =
        NetworkModule.authApi.login(LoginRequest(email, password))
    /*suspend fun login(email: String, password: String): AuthResponse {
        return AuthResponse(
            token = "fake_token",
            user = User(
                id = 1,
                email = email,
                nom = "John Doe",
                role = Role.ADMIN,
                numeroTel = 341234567,
                niveau = "L3",
                parcours = "Info",
                nomFacebook = "john.fb",
                numeroInscription = "123"
            )
        )
    }*/
    suspend fun register(request: RegisterRequest): AuthResponse =
        NetworkModule.authApi.register(request)

    suspend fun me(): User = NetworkModule.authApi.me()

    suspend fun listEvents(): List<Event> = NetworkModule.eventApi.listEvents()
    suspend fun getEvent(id: Long) = NetworkModule.eventApi.getEvent(id)
    suspend fun createEvent(request: EventRequest): Event = NetworkModule.eventApi.createEvent(request)
    suspend fun updateEvent(id: Long, request: EventRequest): Event = NetworkModule.eventApi.updateEvent(id, request)
    suspend fun deleteEvent(id: Long) = NetworkModule.eventApi.deleteEvent(id)

    suspend fun listLocations(): List<Location> = NetworkModule.eventApi.listLocations()
    suspend fun createLocation(request: LocationRequest): Location = NetworkModule.eventApi.createLocation(request)
    suspend fun updateLocation(id: Long, request: LocationRequest): Location = NetworkModule.eventApi.updateLocation(id, request)
    suspend fun deleteLocation(id: Long) = NetworkModule.eventApi.deleteLocation(id)

    suspend fun reserve(eventId: Long): Reservation =
        NetworkModule.reservationApi.createReservation(ReservationRequest(eventId))

    suspend fun myReservations(): List<Reservation> = NetworkModule.reservationApi.myReservations()
    suspend fun allReservations(): List<Reservation> = NetworkModule.reservationApi.allReservations()
    suspend fun cancelReservation(id: Long) = NetworkModule.reservationApi.cancelReservation(id)

    suspend fun ticketByReservation(reservationId: Long): Ticket =
        NetworkModule.ticketApi.getByReservation(reservationId)

    suspend fun validateTicket(ticketId: Long): Ticket =
        NetworkModule.ticketApi.validateTicket(ticketId)

    suspend fun listUsers(): List<User> = NetworkModule.userApi.listUsers()
    suspend fun stats(): UserStatsResponse = NetworkModule.userApi.stats()
    suspend fun createUser(request: RegisterRequest): User = NetworkModule.userApi.createUser(request)
    suspend fun updateUser(id: Long, request: RegisterRequest): User = NetworkModule.userApi.updateUser(id, request)
    suspend fun deleteUser(id: Long) = NetworkModule.userApi.deleteUser(id)
}
