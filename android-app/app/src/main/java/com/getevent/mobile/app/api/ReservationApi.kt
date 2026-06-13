package com.getevent.mobile.app.api

import com.getevent.mobile.app.model.Reservation
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservationApi {
    @GET("api/reservations/me")
    suspend fun myReservations(): List<Reservation>

    @GET("api/reservations")
    suspend fun allReservations(): List<Reservation>

    @POST("api/reservations")
    suspend fun createReservation(@Body request: ReservationRequest): Reservation

    @DELETE("api/reservations/{id}")
    suspend fun cancelReservation(@Path("id") id: Long): MessageResponse
}

data class ReservationRequest(
    val evenementId: Long
)
