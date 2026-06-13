package dto

import kotlinx.serialization.Serializable

@Serializable
data class ReservationRequest(
    val evenementId: Long
)
