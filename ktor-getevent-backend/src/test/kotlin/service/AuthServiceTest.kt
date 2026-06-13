package service

import TestFixtures
import dto.LoginRequest
import model.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AuthServiceTest {

    @Test
    fun loginSucceedsWithValidCredentials() {
        val services = TestFixtures.createServices()
        val email = TestFixtures.uniqueEmail()
        services.userService.create(TestFixtures.studentRequest(email), null)

        val response = services.authService.login(
            LoginRequest(email = email, password = "password123")
        )

        assertTrue(response.token.isNotBlank())
        assertEquals(email, response.user.email)
    }

    @Test
    fun loginFailsWithWrongPassword() {
        val services = TestFixtures.createServices()
        val email = TestFixtures.uniqueEmail()
        services.userService.create(TestFixtures.studentRequest(email), null)

        assertFailsWith<IllegalArgumentException> {
            services.authService.login(LoginRequest(email, "wrong"))
        }
    }

    @Test
    fun loginFailsWithUnknownEmail() {
        val services = TestFixtures.createServices()
        assertFailsWith<IllegalArgumentException> {
            services.authService.login(LoginRequest("unknown@test.local", "password123"))
        }
    }

    @Test
    fun registerCreatesStudentWithToken() {
        val services = TestFixtures.createServices()
        val email = TestFixtures.uniqueEmail()
        val response = services.authService.register(TestFixtures.studentRequest(email), null)
        assertTrue(response.token.isNotBlank())
        assertEquals(Role.STUDENT, response.user.role)
        assertEquals(email, response.user.email)
    }
}
