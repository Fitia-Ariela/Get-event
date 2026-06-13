package com.getevent.mobile.app.api

import com.getevent.mobile.app.model.Role
import com.getevent.mobile.app.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("api/auth/me")
    suspend fun me(): User
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val numeroInscription: String,
    val nom: String,
    val niveau: String,
    val parcours: String,
    val numeroTel: Long,
    val email: String,
    val nomFacebook: String,
    val password: String,
    val role: Role = Role.STUDENT
)

data class AuthResponse(
    val token: String,
    val user: User
)
