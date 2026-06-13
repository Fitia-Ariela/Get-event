package repository.auth

import model.User
import repository.user.UserRepository

class AuthRepository(private val userRepository: UserRepository) {
    fun findByEmail(email: String): User? = userRepository.findByEmail(email)
}
