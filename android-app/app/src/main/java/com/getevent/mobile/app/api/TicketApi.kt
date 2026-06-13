package com.getevent.mobile.app.api

import com.getevent.mobile.app.model.Ticket
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface TicketApi {
    @GET("api/tickets/reservation/{reservationId}")
    suspend fun getByReservation(@Path("reservationId") reservationId: Long): Ticket

    @PUT("api/tickets/{id}/use")
    suspend fun validateTicket(@Path("id") id: Long): Ticket
}
