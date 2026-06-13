package service

import dto.AuthResponse
import dto.LoginRequest
import dto.RegisterRequest
import dto.toResponse
import model.Role
import model.User
import repository.user.UserRepository
import util.JwtConfig
import util.PasswordHasher

class AuthService(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val jwtConfig: JwtConfig
) {

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Email ou mot de passe incorrect")
        if (!PasswordHasher.verify(request.password, user.passwordHash)) {
            throw IllegalArgumentException("Email ou mot de passe incorrect")
        }
        return AuthResponse(
            token = jwtConfig.generateToken(user),
            user = user.toResponse()
        )
    }

    fun register(request: RegisterRequest, requesterRole: Role?): AuthResponse {
        val user = userService.create(request, requesterRole)
        return AuthResponse(
            token = jwtConfig.generateToken(user),
            user = user.toResponse()
        )
    }
}
