package service

import TestFixtures
import dto.ReservationRequest
import dto.TransactionRequest
import model.ReservationStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ReservationServiceTest {

    @Test
    fun publicEventReservationIsConfirmedWithTicket() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        val student = services.userService.create(TestFixtures.studentRequest(), null)

        val detail = services.reservationService.create(
            student.id,
            ReservationRequest(event.event.idEvenement)
        )

        assertEquals(ReservationStatus.CONFIRMED, detail.reservation.statut)
        assertNotNull(detail.ticket)
    }

    @Test
    fun privateEventReservationStaysPendingWithoutTicket() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.privateEventRequest(lieu.id))
        val student = services.userService.create(TestFixtures.studentRequest(), null)

        val detail = services.reservationService.create(
            student.id,
            ReservationRequest(event.event.idEvenement)
        )

        assertEquals(ReservationStatus.PENDING, detail.reservation.statut)
        assertNull(detail.ticket)
    }

    @Test
    fun duplicateReservationRejected() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        val student = services.userService.create(TestFixtures.studentRequest(), null)
        val request = ReservationRequest(event.event.idEvenement)

        services.reservationService.create(student.id, request)
        assertFailsWith<IllegalStateException> {
            services.reservationService.create(student.id, request)
        }
    }

    @Test
    fun cancelReservationSetsCancelledStatus() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        val student = services.userService.create(TestFixtures.studentRequest(), null)
        val reservation = services.reservationService.create(
            student.id,
            ReservationRequest(event.event.idEvenement)
        )

        val cancelled = services.reservationService.cancel(
            reservation.reservation.idReservation,
            student.id,
            isAdmin = false
        )
        assertEquals(ReservationStatus.CANCELLED, cancelled.reservation.statut)
    }

    @Test
    fun studentCannotAccessOtherReservation() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        val student1 = services.userService.create(TestFixtures.studentRequest(), null)
        val student2 = services.userService.create(TestFixtures.studentRequest(), null)
        val reservation = services.reservationService.create(
            student1.id,
            ReservationRequest(event.event.idEvenement)
        )

        assertFailsWith<IllegalAccessException> {
            services.reservationService.getById(
                reservation.reservation.idReservation,
                student2.id,
                isStaff = false
            )
        }
    }

    @Test
    fun paymentConfirmsPrivateReservationAndCreatesTicket() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.privateEventRequest(lieu.id))
        val student = services.userService.create(TestFixtures.studentRequest(), null)
        val reservation = services.reservationService.create(
            student.id,
            ReservationRequest(event.event.idEvenement)
        )

        services.transactionService.pay(
            student.id,
            TransactionRequest(
                reservationId = reservation.reservation.idReservation,
                modePaiement = "MVola",
                referencePaiement = "REF-001"
            )
        )

        val afterPay = services.reservationService.getById(
            reservation.reservation.idReservation,
            student.id,
            isStaff = false
        )
        assertEquals(ReservationStatus.CONFIRMED, afterPay.reservation.statut)
        assertNotNull(afterPay.ticket)
        assertNotNull(afterPay.transaction)
    }
}
