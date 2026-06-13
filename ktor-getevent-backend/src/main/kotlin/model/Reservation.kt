package model

import kotlinx.serialization.Serializable

@Serializable
enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}

@Serializable
data class Reservation(
    val idReservation: Long = 0,
    val dateReservation: String,
    val statut: ReservationStatus,
    val utilisateurId: Long,
    val evenementId: Long
)
