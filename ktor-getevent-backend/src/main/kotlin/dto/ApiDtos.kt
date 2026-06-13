package dto

import model.*
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse
)

@Serializable
data class UserResponse(
    val id: Long,
    val numeroInscription: String,
    val nom: String,
    val niveau: String,
    val parcours: String,
    val numeroTel: Long,
    val email: String,
    val nomFacebook: String,
    val role: Role
)

@Serializable
data class LocationRequest(
    val nom: String,
    val longitude: Float,
    val latitude: Float,
    val capacite: Int
)

@Serializable
data class TransactionRequest(
    val reservationId: Long,
    val modePaiement: String,
    val referencePaiement: String
)

@Serializable
data class EventDetailResponse(
    val event: Event,
    val lieu: Location?
)

@Serializable
data class ReservationDetailResponse(
    val reservation: Reservation,
    val evenement: Event?,
    val transaction: Transaction?,
    val ticket: Ticket?
)

@Serializable
data class UserStatsResponse(
    val total: Int,
    val parRole: Map<String, Int>
)

@Serializable
data class MessageResponse(val message: String)

@Serializable
data class ErrorResponse(val error: String)

fun User.toResponse() = UserResponse(
    id = id,
    numeroInscription = numeroInscription,
    nom = nom,
    niveau = niveau,
    parcours = parcours,
    numeroTel = numeroTel,
    email = email,
    nomFacebook = nomFacebook,
    role = role
)
