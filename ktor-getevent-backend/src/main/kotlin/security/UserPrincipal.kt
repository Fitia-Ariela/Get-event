package security

import model.Role

data class UserPrincipal(
    val userId: Long,
    val email: String,
    val role: Role
)
