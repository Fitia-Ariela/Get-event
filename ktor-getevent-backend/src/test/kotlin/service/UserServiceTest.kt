package service

import TestFixtures
import model.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UserServiceTest {

    @Test
    fun createStudentWithoutRequester() {
        val services = TestFixtures.createServices()
        val user = services.userService.create(TestFixtures.studentRequest(), null)
        assertEquals(Role.STUDENT, user.role)
    }

    @Test
    fun publicRegistrationRejectsBoardMemberRole() {
        val services = TestFixtures.createServices()
        assertFailsWith<IllegalArgumentException> {
            services.userService.create(
                TestFixtures.studentRequest().copy(role = Role.BOARD_MEMBER),
                null
            )
        }
    }

    @Test
    fun adminCanCreateBoardMember() {
        val services = TestFixtures.createServices()
        TestFixtures.seedAdmin(services.store)
        val member = services.userService.create(
            TestFixtures.studentRequest().copy(
                email = TestFixtures.uniqueEmail(),
                role = Role.BOARD_MEMBER
            ),
            Role.ADMIN
        )
        assertEquals(Role.BOARD_MEMBER, member.role)
    }

    @Test
    fun duplicateEmailRejected() {
        val services = TestFixtures.createServices()
        val email = TestFixtures.uniqueEmail()
        services.userService.create(TestFixtures.studentRequest(email), null)
        assertFailsWith<IllegalArgumentException> {
            services.userService.create(TestFixtures.studentRequest(email), null)
        }
    }

    @Test
    fun statsCountsUsersByRole() {
        val services = TestFixtures.createServices()
        services.userService.create(TestFixtures.studentRequest(), null)
        services.userService.create(TestFixtures.studentRequest(), null)
        val stats = services.userService.stats()
        assertEquals(2, stats.total)
        assertEquals(2, stats.parRole["STUDENT"])
    }

    @Test
    fun deleteRemovesUser() {
        val services = TestFixtures.createServices()
        val user = services.userService.create(TestFixtures.studentRequest(), null)
        services.userService.delete(user.id)
        assertEquals(null, services.userService.findById(user.id))
    }

    @Test
    fun updateByAdminChangesRole() {
        val services = TestFixtures.createServices()
        TestFixtures.seedAdmin(services.store)
        val student = services.userService.create(TestFixtures.studentRequest(), null)
        val updated = services.userService.update(
            student.id,
            TestFixtures.studentRequest(student.email).copy(role = Role.BOARD_MEMBER),
            Role.ADMIN
        )
        assertEquals(Role.BOARD_MEMBER, updated.role)
    }

    @Test
    fun deleteUnknownUserThrows() {
        val services = TestFixtures.createServices()
        assertFailsWith<NoSuchElementException> {
            services.userService.delete(9999)
        }
    }
}
