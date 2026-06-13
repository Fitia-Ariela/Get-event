package service

import datastore.MemoryStore
import dto.EventDetailResponse
import dto.EventRequest
import dto.LocationRequest
import model.Event
import model.Location
import repository.event.EventRepository
import repository.event.LocationRepository
import repository.reservation.ReservationRepository

class EventService(
    private val eventRepository: EventRepository,
    private val locationRepository: LocationRepository,
    private val reservationRepository: ReservationRepository,
    private val store: MemoryStore
) {

    /**fun listEvents(): List<EventDetailResponse> =
        eventRepository.findAll().map { toDetail(it) }**/

    fun listEvents(): List<Event> =
        eventRepository.findAll()

    fun getEvent(id: Long): EventDetailResponse {
        val event = eventRepository.findById(id)
            ?: throw NoSuchElementException("Événement introuvable")
        return toDetail(event)
    }

    fun createEvent(request: EventRequest): EventDetailResponse {
        validateLocation(request.lieuId)
        println(request.lieuId)
        if (request.estPrive && request.tarif <= 0) {
            throw IllegalArgumentException("Un événement privé doit avoir un tarif positif")
        }
        val event = Event(
            idEvenement = store.nextEventId(),
            nomEvenement = request.nomEvenement,
            dateEvenement = request.dateEvenement,
            lieuId = request.lieuId,
            description = request.description,
            estPrive = request.estPrive,
            tarif = request.tarif
        )
        return toDetail(eventRepository.save(event))
    }

    fun updateEvent(id: Long, request: EventRequest): EventDetailResponse {
        val existing = eventRepository.findById(id)
            ?: throw NoSuchElementException("Événement introuvable")
        validateLocation(request.lieuId)
        val updated = existing.copy(
            nomEvenement = request.nomEvenement,
            dateEvenement = request.dateEvenement,
            lieuId = request.lieuId,
            description = request.description,
            estPrive = request.estPrive,
            tarif = request.tarif
        )
        return toDetail(eventRepository.save(updated))
    }

    fun deleteEvent(id: Long) {
        if (!eventRepository.delete(id)) {
            throw NoSuchElementException("Événement introuvable")
        }
    }

    fun listLocations(): List<Location> = locationRepository.findAll()

    fun createLocation(request: LocationRequest): Location {
        val location = Location(
            id = store.nextLocationId(),
            nom = request.nom,
            longitude = request.longitude,
            latitude = request.latitude,
            capacite = request.capacite
        )
        return locationRepository.save(location)
    }

    fun updateLocation(id: Long, request: LocationRequest): Location {
        val existing = locationRepository.findById(id)
            ?: throw NoSuchElementException("Lieu introuvable")
        return locationRepository.save(
            existing.copy(
                nom = request.nom,
                longitude = request.longitude,
                latitude = request.latitude,
                capacite = request.capacite
            )
        )
    }

    fun deleteLocation(id: Long) {
        if (locationRepository.findById(id) == null) {
            throw NoSuchElementException("Lieu introuvable")
        }
        if (eventRepository.findByLocation(id).isNotEmpty()) {
            throw IllegalStateException("Impossible de supprimer un lieu utilisé par des événements")
        }
        locationRepository.delete(id)
    }

    fun availablePlaces(eventId: Long): Int {
        val event = eventRepository.findById(eventId)
            ?: throw NoSuchElementException("Événement introuvable")
        val location = locationRepository.findById(event.lieuId)
            ?: throw NoSuchElementException("Lieu introuvable")
        val reserved = reservationRepository.countByEvent(eventId)
        return (location.capacite - reserved).coerceAtLeast(0)
    }

    private fun validateLocation(lieuId: Long) {
        if (locationRepository.findById(lieuId) == null) {
            throw IllegalArgumentException("Lieu introuvable")
        }
    }

    private fun toDetail(event: Event) = EventDetailResponse(
        event = event,
        lieu = locationRepository.findById(event.lieuId)
    )
}
