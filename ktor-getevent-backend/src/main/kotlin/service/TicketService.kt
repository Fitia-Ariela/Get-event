package service

import datastore.MemoryStore
import model.ReservationStatus
import model.Ticket
import model.Reservation
import repository.ticket.TicketRepository
import util.DateTimeUtil
import java.util.UUID

class TicketService(
    private val ticketRepository: TicketRepository,
    private val store: MemoryStore
) {

    fun findByReservation(reservationId: Long): Ticket? =
        ticketRepository.findByReservation(reservationId)

    fun findById(id: Long): Ticket =
        ticketRepository.findById(id) ?: throw NoSuchElementException("Ticket introuvable")

    fun generateForReservation(reservation: Reservation): Ticket {
        if (reservation.statut != ReservationStatus.CONFIRMED) {
            throw IllegalStateException("La réservation doit être confirmée pour générer un ticket")
        }
        ticketRepository.findByReservation(reservation.idReservation)?.let { return it }

        val ticket = Ticket(
            idTicket = store.nextTicketId(),
            urlCode = "TICKET-${UUID.randomUUID()}",
            dateGeneration = DateTimeUtil.nowIso(),
            estUtilise = false,
            reservationId = reservation.idReservation
        )
        return ticketRepository.save(ticket)
    }

    fun markAsUsed(ticketId: Long): Ticket {
        val ticket = findById(ticketId)
        return ticketRepository.save(ticket.copy(estUtilise = true))
    }
}
