package dto

import model.Role
import kotlinx.serialization.Serializable

@Serializable
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
