package repository

import datastore.MemoryStore
import model.Role
import model.User
import util.PasswordHasher
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryStoreTest {

    @Test
    fun persistsAndReloadsUsers() {
        val file = File.createTempFile("store-persist-", ".json")
        file.deleteOnExit()

        val store1 = MemoryStore(file, seedAdmin = false)
        store1.users.add(
            User(
                id = store1.nextUserId(),
                numeroInscription = "X",
                nom = "Test",
                niveau = "L1",
                parcours = "Info",
                numeroTel = 1,
                email = "persist@test.local",
                nomFacebook = "fb",
                role = Role.STUDENT,
                passwordHash = PasswordHasher.hash("pwd")
            )
        )
        store1.persist()

        val store2 = MemoryStore(file, seedAdmin = false)
        assertEquals(1, store2.users.size)
        assertEquals("persist@test.local", store2.users.first().email)
    }

    @Test
    fun seedAdminCreatesDefaultAdminWhenEnabled() {
        val file = File.createTempFile("store-seed-", ".json")
        file.deleteOnExit()
        val store = MemoryStore(file, seedAdmin = true)
        assertEquals(1, store.users.size)
        assertEquals("admin@getevent.local", store.users.first().email)
    }
}
