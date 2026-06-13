package repository.user

import datastore.MemoryStore
import model.User

class UserRepository(private val store: MemoryStore) {

    fun findAll(): List<User> = store.users.toList()

    fun findById(id: Long): User? = store.users.find { it.id == id }

    fun findByEmail(email: String): User? = store.users.find { it.email.equals(email, ignoreCase = true) }

    fun save(user: User): User = store.mutate {
        val index = users.indexOfFirst { it.id == user.id }
        if (index >= 0) users[index] = user else users.add(user)
        user
    }

    fun delete(id: Long): Boolean = store.mutate {
        users.removeIf { it.id == id }
    }
}
