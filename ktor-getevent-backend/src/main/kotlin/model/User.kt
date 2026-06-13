package model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0,
    val numeroInscription: String,
    val nom: String,
    val niveau: String,
    val parcours: String,
    val numeroTel: Long,
    val email: String,
    val nomFacebook: String,
    val role: Role,
    val passwordHash: String
)
