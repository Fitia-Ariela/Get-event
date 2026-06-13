package service

import datastore.MemoryStore
import dto.TransactionRequest
import model.PaymentStatus
import model.Transaction
import repository.event.EventRepository
import repository.reservation.ReservationRepository
import repository.transaction.TransactionRepository
import util.DateTimeUtil

class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val reservationRepository: ReservationRepository,
    private val eventRepository: EventRepository,
    private val ticketService: TicketService,
    private val store: MemoryStore
) {

    fun findByReservation(reservationId: Long): Transaction? =
        transactionRepository.findByReservation(reservationId)

    fun pay(userId: Long, request: TransactionRequest): Transaction {
        val reservation = reservationRepository.findById(request.reservationId)
            ?: throw NoSuchElementException("Réservation introuvable")

        if (reservation.utilisateurId != userId) {
            throw IllegalAccessException("Accès refusé")
        }

        val event = eventRepository.findById(reservation.evenementId)
            ?: throw NoSuchElementException("Événement introuvable")

        if (!event.estPrive) {
            throw IllegalStateException("Cet événement public ne nécessite pas de paiement")
        }

        if (transactionRepository.findByReservation(reservation.idReservation) != null) {
            throw IllegalStateException("Une transaction existe déjà pour cette réservation")
        }

        val transaction = Transaction(
            idTransaction = store.nextTransactionId(),
            montant = event.tarif,
            dateTransaction = DateTimeUtil.nowIso(),
            modePaiement = request.modePaiement,
            statutPaiement = PaymentStatus.COMPLETED,
            referencePaiement = request.referencePaiement,
            reservationId = reservation.idReservation
        )

        val saved = transactionRepository.save(transaction)
        val confirmed = reservation.copy(statut = model.ReservationStatus.CONFIRMED)
        val savedReservation = reservationRepository.save(confirmed)
        ticketService.generateForReservation(savedReservation)
        return saved
    }
}
