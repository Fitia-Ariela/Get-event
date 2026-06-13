package repository.ticket

import datastore.MemoryStore
import model.Ticket

class TicketRepository(private val store: MemoryStore) {

    fun findAll(): List<Ticket> = store.tickets.toList()

    fun findById(id: Long): Ticket? = store.tickets.find { it.idTicket == id }

    fun findByReservation(reservationId: Long): Ticket? =
        store.tickets.find { it.reservationId == reservationId }

    fun save(ticket: Ticket): Ticket = store.mutate {
        val index = tickets.indexOfFirst { it.idTicket == ticket.idTicket }
        if (index >= 0) tickets[index] = ticket else tickets.add(ticket)
        ticket
    }
}
