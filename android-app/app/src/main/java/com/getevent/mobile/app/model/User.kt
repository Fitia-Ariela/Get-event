package com.getevent.mobile.app.model

data class User(
    val id: Long = 0,
    val numeroInscription: String,
    val nom: String,
    val niveau: String,
    val parcours: String,
    val numeroTel: Long,
    val email: String,
    val nomFacebook: String,
    val role: Role
)

enum class Role {
    ADMIN,
    BOARD_MEMBER,
    STUDENT
}
