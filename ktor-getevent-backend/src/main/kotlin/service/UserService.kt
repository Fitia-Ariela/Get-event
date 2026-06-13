package service

import datastore.MemoryStore
import dto.RegisterRequest
import dto.UserStatsResponse
import model.Role
import model.User
import repository.user.UserRepository
import util.PasswordHasher

class UserService(
    private val userRepository: UserRepository,
    private val store: MemoryStore
) {

    fun findAll(): List<User> = userRepository.findAll()

    fun findById(id: Long): User? = userRepository.findById(id)

    fun stats(): UserStatsResponse {
        val all = userRepository.findAll()
        return UserStatsResponse(
            total = all.size,
            parRole = all.groupingBy { it.role.name }.eachCount()
        )
    }

    fun create(request: RegisterRequest, requesterRole: Role?): User {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Cet email est déjà utilisé")
        }
        val role = validateRoleForCreation(request.role, requesterRole)
        val user = User(
            id = store.nextUserId(),
            numeroInscription = request.numeroInscription,
            nom = request.nom,
            niveau = request.niveau,
            parcours = request.parcours,
            numeroTel = request.numeroTel,
            email = request.email,
            nomFacebook = request.nomFacebook,
            role = role,
            passwordHash = PasswordHasher.hash(request.password)
        )
        return userRepository.save(user)
    }

    fun update(id: Long, request: RegisterRequest, requesterRole: Role): User {
        val existing = userRepository.findById(id)
            ?: throw NoSuchElementException("Utilisateur introuvable")
        if (requesterRole != Role.ADMIN && existing.id != id) {
            throw IllegalAccessException("Accès refusé")
        }
        val role = if (requesterRole == Role.ADMIN) request.role else existing.role
        val updated = existing.copy(
            numeroInscription = request.numeroInscription,
            nom = request.nom,
            niveau = request.niveau,
            parcours = request.parcours,
            numeroTel = request.numeroTel,
            email = request.email,
            nomFacebook = request.nomFacebook,
            role = role,
            passwordHash = if (request.password.isNotBlank()) {
                PasswordHasher.hash(request.password)
            } else existing.passwordHash
        )
        return userRepository.save(updated)
    }

    fun delete(id: Long) {
        if (!userRepository.delete(id)) {
            throw NoSuchElementException("Utilisateur introuvable")
        }
    }

    private fun validateRoleForCreation(requested: Role, requesterRole: Role?): Role = when {
        requesterRole == null -> {
            if (requested != Role.STUDENT) {
                throw IllegalArgumentException("L'inscription publique est réservée aux étudiants")
            }
            Role.STUDENT
        }
        requested == Role.ADMIN ->
            throw IllegalArgumentException("Impossible de créer un administrateur via l'API")
        requested == Role.BOARD_MEMBER && requesterRole != Role.ADMIN ->
            throw IllegalArgumentException("Seul un administrateur peut créer un membre de bureau")
        else -> requested
    }
}
