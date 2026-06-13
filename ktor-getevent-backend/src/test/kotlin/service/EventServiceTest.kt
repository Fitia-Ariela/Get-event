package service

import TestFixtures
import dto.ReservationRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EventServiceTest {

    @Test
    fun createLocationAndPublicEvent() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))

        assertEquals("Conférence publique", event.event.nomEvenement)
        assertEquals(lieu.id, event.lieu?.id)
        assertEquals(1, services.eventService.listEvents().size)
    }

    @Test
    fun privateEventRequiresPositiveTarif() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        assertFailsWith<IllegalArgumentException> {
            services.eventService.createEvent(
                TestFixtures.privateEventRequest(lieu.id, tarif = 0.0)
            )
        }
    }

    @Test
    fun updateEventChangesFields() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val created = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        val updated = services.eventService.updateEvent(
            created.event.idEvenement,
            TestFixtures.publicEventRequest(lieu.id).copy(nomEvenement = "Nouveau nom")
        )
        assertEquals("Nouveau nom", updated.event.nomEvenement)
    }

    @Test
    fun deleteEventRemovesIt() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        val event = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        services.eventService.deleteEvent(event.event.idEvenement)
        assertFailsWith<NoSuchElementException> {
            services.eventService.getEvent(event.event.idEvenement)
        }
    }

    @Test
    fun cannotDeleteLocationUsedByEvent() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest())
        services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        assertFailsWith<IllegalStateException> {
            services.eventService.deleteLocation(lieu.id)
        }
    }

    @Test
    fun availablePlacesDecreaseAfterReservation() {
        val services = TestFixtures.createServices()
        val lieu = services.eventService.createLocation(TestFixtures.locationRequest().copy(capacite = 2))
        val event = services.eventService.createEvent(TestFixtures.publicEventRequest(lieu.id))
        val student = services.userService.create(TestFixtures.studentRequest(), null)

        assertEquals(2, services.eventService.availablePlaces(event.event.idEvenement))
        services.reservationService.create(
            student.id,
            ReservationRequest(event.event.idEvenement)
        )
        assertEquals(1, services.eventService.availablePlaces(event.event.idEvenement))
    }

    @Test
    fun createEventFailsForUnknownLocation() {
        val services = TestFixtures.createServices()
        assertFailsWith<IllegalArgumentException> {
            services.eventService.createEvent(TestFixtures.publicEventRequest(999))
        }
    }
}
