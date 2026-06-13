package com.getevent.mobile.app.model

data class Reservation(
    val idReservation: Long = 0,
    val dateReservation: String,
    val statut: ReservationStatus,
    val utilisateurId: Long,
    val evenementId: Long
)

enum class ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
