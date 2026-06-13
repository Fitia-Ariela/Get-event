package model

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: Long = 0,
    val nom: String,
    val longitude: Float,
    val latitude: Float,
    val capacite: Int
)
