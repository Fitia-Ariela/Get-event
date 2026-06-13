package dto

import kotlinx.serialization.Serializable

@Serializable
data class EventRequest(
    val nomEvenement: String,
    val dateEvenement: String,
    val lieuId: Long = 1,
    val description: String,
    val estPrive: Boolean = false,
    val tarif: Double = 0.0
)
