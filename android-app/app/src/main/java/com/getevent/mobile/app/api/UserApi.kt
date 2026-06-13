package com.getevent.mobile.app.api

import com.getevent.mobile.app.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("api/users")
    suspend fun listUsers(): List<User>

    @GET("api/users/stats")
    suspend fun stats(): UserStatsResponse

    @POST("api/users")
    suspend fun createUser(@Body request: RegisterRequest): User

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body request: RegisterRequest): User

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): MessageResponse
}

data class UserStatsResponse(
    val total: Int,
    val parRole: Map<String, Int>
)
