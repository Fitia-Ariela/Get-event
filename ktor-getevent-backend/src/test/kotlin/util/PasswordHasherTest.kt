package util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordHasherTest {

    @Test
    fun hashIsDeterministic() {
        val hash1 = PasswordHasher.hash("secret")
        val hash2 = PasswordHasher.hash("secret")
        assertTrue(hash1 == hash2)
        assertTrue(hash1.isNotEmpty())
    }

    @Test
    fun verifyAcceptsCorrectPassword() {
        val hash = PasswordHasher.hash("motdepasse")
        assertTrue(PasswordHasher.verify("motdepasse", hash))
    }

    @Test
    fun verifyRejectsWrongPassword() {
        val hash = PasswordHasher.hash("motdepasse")
        assertFalse(PasswordHasher.verify("autre", hash))
    }

    @Test
    fun differentPasswordsProduceDifferentHashes() {
        val h1 = PasswordHasher.hash("a")
        val h2 = PasswordHasher.hash("b")
        assertNotEquals(h1, h2)
    }
}
