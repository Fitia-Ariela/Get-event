package model

import kotlinx.serialization.Serializable

@Serializable
data class Ticket(
    val idTicket: Long = 0,
    val urlCode: String,
    val dateGeneration: String,
    val estUtilise: Boolean = false,
    val reservationId: Long
)
