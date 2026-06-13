package service

import TestFixtures
import dto.ReservationRequest
import model.Reservation
import model.ReservationStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TicketServiceTest {

    @Test
    fun generateTicketForConfirmedReservation() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        val student = services.userService.create(TestFixtures.studentRequest(), null)
        val reservation = services.reservationService.create(
            student.id,
            ReservationRequest(event.event.idEvenement)
        )

        val ticket = services.ticketService.findByReservation(reservation.reservation.idReservation)
        assertNotNull(ticket)
        assertTrue(ticket.urlCode.startsWith("TICKET-"))
        assertFalse(ticket.estUtilise)
    }

    @Test
    fun generateFailsForPendingReservation() {
        val services = TestFixtures.createServices()
        val pending = Reservation(
            idReservation = 1,
            dateReservation = "2026-01-01T00:00:00Z",
            statut = ReservationStatus.PENDING,
            utilisateurId = 1,
            evenementId = 1
        )
        assertFailsWith<IllegalStateException> {
            services.ticketService.generateForReservation(pending)
        }
    }

    @Test
    fun markAsUsedSetsFlag() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        val student = services.userService.create(TestFixtures.studentRequest(), null)
        val reservation = services.reservationService.create(
            student.id,
            ReservationRequest(event.event.idEvenement)
        )
        val ticket = services.ticketService.findByReservation(reservation.reservation.idReservation)!!

        val used = services.ticketService.markAsUsed(ticket.idTicket)
        assertTrue(used.estUtilise)
    }

    @Test
    fun findByIdThrowsWhenMissing() {
        val services = TestFixtures.createServices()
        assertFailsWith<NoSuchElementException> {
            services.ticketService.findById(9999)
        }
    }
}
