package com.getevent.mobile.app.api

import com.getevent.mobile.app.model.Event
import com.getevent.mobile.app.model.Location
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EventApi {
    @GET("api/events")
    suspend fun listEvents(): List<Event>

    @GET("api/events/{id}")
    suspend fun getEvent(@Path("id") id: Long): EventDetailResponse

    @POST("api/events")
    suspend fun createEvent(@Body request: EventRequest): Event

    @PUT("api/events/{id}")
    suspend fun updateEvent(@Path("id") id: Long, @Body request: EventRequest): Event

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): MessageResponse

    @GET("api/locations")
    suspend fun listLocations(): List<Location>

    @POST("api/locations")
    suspend fun createLocation(@Body request: LocationRequest): Location

    @PUT("api/locations/{id}")
    suspend fun updateLocation(@Path("id") id: Long, @Body request: LocationRequest): Location

    @DELETE("api/locations/{id}")
    suspend fun deleteLocation(@Path("id") id: Long): MessageResponse
}

data class EventRequest(
    val nomEvenement: String,
    val dateEvenement: String,
    val lieuId: Long,
    val description: String,
    val estPrive: Boolean = false,
    val tarif: Double = 0.0
)

data class LocationRequest(
    val nom: String,
    val longitude: Float,
    val latitude: Float,
    val capacite: Int
)

data class EventDetailResponse(
    val event: Event,
    val lieu: Location?
)

data class MessageResponse(val message: String)
