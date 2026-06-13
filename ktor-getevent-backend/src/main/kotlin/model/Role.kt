package model

import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    ADMIN,
    BOARD_MEMBER,
    STUDENT
}
