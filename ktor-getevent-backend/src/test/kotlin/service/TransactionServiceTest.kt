package service

import TestFixtures
import dto.ReservationRequest
import dto.TransactionRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TransactionServiceTest {

    @Test
    fun payPrivateEventSucceeds() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.privateEventRequest(lieu.id, 3000.0))
        val student = services.userService.create(TestFixtures.studentRequest(), null)
        val reservation = services.reservationService.create(
            student.id,
            ReservationRequest(event.event.idEvenement)
        )

        val transaction = services.transactionService.pay(
            student.id,
            TransactionRequest(
                reservationId = reservation.reservation.idReservation,
                modePaiement = "Orange Money",
                referencePaiement = "OM-123"
            )
        )

        assertEquals(3000.0, transaction.montant)
        assertEquals(reservation.reservation.idReservation, transaction.reservationId)
    }

    @Test
    fun payPublicEventRejected() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        val student = services.userService.create(TestFixtures.studentRequest(), null)
        val reservation = services.reservationService.create(
            student.id,
            ReservationRequest(event.event.idEvenement)
        )

        assertFailsWith<IllegalStateException> {
            services.transactionService.pay(
                student.id,
                TransactionRequest(
                    reservation.reservation.idReservation,
                    "Cash",
                    "REF"
                )
            )
        }
    }

    @Test
    fun payTwiceRejected() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.privateEventRequest(lieu.id))
        val student = services.userService.create(TestFixtures.studentRequest(), null)
        val reservation = services.reservationService.create(
            student.id,
            ReservationRequest(event.event.idEvenement)
        )
        val request = TransactionRequest(
            reservation.reservation.idReservation,
            "MVola",
            "REF-1"
        )
        services.transactionService.pay(student.id, request)
        assertFailsWith<IllegalStateException> {
            services.transactionService.pay(student.id, request)
        }
    }

    @Test
    fun payOtherUserReservationRejected() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.privateEventRequest(lieu.id))
        val student1 = services.userService.create(TestFixtures.studentRequest(), null)
        val student2 = services.userService.create(TestFixtures.studentRequest(), null)
        val reservation = services.reservationService.create(
            student1.id,
            ReservationRequest(event.event.idEvenement)
        )

        assertFailsWith<IllegalAccessException> {
            services.transactionService.pay(
                student2.id,
                TransactionRequest(
                    reservation.reservation.idReservation,
                    "MVola",
                    "REF"
                )
            )
        }
    }
}
