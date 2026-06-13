package repository.reservation

import datastore.MemoryStore
import model.Reservation

class ReservationRepository(private val store: MemoryStore) {

    fun findAll(): List<Reservation> = store.reservations.toList()

    fun findById(id: Long): Reservation? = store.reservations.find { it.idReservation == id }

    fun findByUser(userId: Long): List<Reservation> =
        store.reservations.filter { it.utilisateurId == userId }

    fun findByEvent(eventId: Long): List<Reservation> =
        store.reservations.filter { it.evenementId == eventId }

    fun countByEvent(eventId: Long): Int =
        store.reservations.count { it.evenementId == eventId }

    fun save(reservation: Reservation): Reservation = store.mutate {
        val index = reservations.indexOfFirst { it.idReservation == reservation.idReservation }
        if (index >= 0) reservations[index] = reservation else reservations.add(reservation)
        reservation
    }

    fun delete(id: Long): Boolean = store.mutate {
        reservations.removeIf { it.idReservation == id }
    }
}
