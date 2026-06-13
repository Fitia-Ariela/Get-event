package service

import datastore.MemoryStore
import dto.ReservationDetailResponse
import dto.ReservationRequest
import model.Reservation
import model.ReservationStatus
import repository.event.EventRepository
import repository.reservation.ReservationRepository
import repository.ticket.TicketRepository
import repository.transaction.TransactionRepository
import util.DateTimeUtil
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val eventRepository: EventRepository,
    private val ticketRepository: TicketRepository,
    private val transactionRepository: TransactionRepository,
    private val eventService: EventService,
    private val ticketService: TicketService,
    private val store: MemoryStore
) {

    /**fun listAll(): List<ReservationDetailResponse> =
        reservationRepository.findAll().map { toDetail(it) }**/

    fun listAll(): List<Reservation> =
        reservationRepository.findAll()

    fun listByUser(userId: Long): List<ReservationDetailResponse> =
        reservationRepository.findByUser(userId).map { toDetail(it) }

    fun getById(id: Long, userId: Long?, isStaff: Boolean): ReservationDetailResponse {
        val reservation = reservationRepository.findById(id)
            ?: throw NoSuchElementException("Réservation introuvable")
        if (!isStaff && reservation.utilisateurId != userId) {
            throw IllegalAccessException("Accès refusé")
        }
        return toDetail(reservation)
    }

    fun create(userId: Long, request: ReservationRequest): ReservationDetailResponse {
        val event = eventRepository.findById(request.evenementId)
            ?: throw NoSuchElementException("Événement introuvable")

        if (eventService.availablePlaces(event.idEvenement) <= 0) {
            throw IllegalStateException("Plus de places disponibles pour cet événement")
        }

        val existing = reservationRepository.findByUser(userId)
            .any { it.evenementId == event.idEvenement && it.statut != ReservationStatus.CANCELLED }
        if (existing) {
            throw IllegalStateException("Vous avez déjà une réservation pour cet événement")
        }

        val statut = if (event.estPrive) ReservationStatus.PENDING else ReservationStatus.CONFIRMED

        val reservation = Reservation(
            idReservation = store.nextReservationId(),
            dateReservation = DateTimeUtil.nowIso(),
            statut = statut,
            utilisateurId = userId,
            evenementId = event.idEvenement
        )
        val saved = reservationRepository.save(reservation)

        if (!event.estPrive) {
            ticketService.generateForReservation(saved)
        }

        return toDetail(saved)
    }

    fun cancel(reservationId: Long, userId: Long, isAdmin: Boolean): ReservationDetailResponse {
        val reservation = reservationRepository.findById(reservationId)
            ?: throw NoSuchElementException("Réservation introuvable")
        if (!isAdmin && reservation.utilisateurId != userId) {
            throw IllegalAccessException("Accès refusé")
        }
        val updated = reservation.copy(statut = ReservationStatus.CANCELLED)
        return toDetail(reservationRepository.save(updated))
    }

    fun confirmAfterPayment(reservationId: Long): ReservationDetailResponse {
        val reservation = reservationRepository.findById(reservationId)
            ?: throw NoSuchElementException("Réservation introuvable")
        val event = eventRepository.findById(reservation.evenementId)
            ?: throw NoSuchElementException("Événement introuvable")

        if (!event.estPrive) {
            throw IllegalStateException("Cet événement ne nécessite pas de paiement")
        }

        val transaction = transactionRepository.findByReservation(reservationId)
            ?: throw IllegalStateException("Aucune transaction associée")

        if (transaction.statutPaiement != model.PaymentStatus.COMPLETED) {
            throw IllegalStateException("Le paiement n'est pas encore validé")
        }

        val confirmed = reservation.copy(statut = ReservationStatus.CONFIRMED)
        val saved = reservationRepository.save(confirmed)
        ticketService.generateForReservation(saved)
        return toDetail(saved)
    }

    private fun toDetail(reservation: Reservation) = ReservationDetailResponse(
        reservation = reservation,
        evenement = eventRepository.findById(reservation.evenementId),
        transaction = transactionRepository.findByReservation(reservation.idReservation),
        ticket = ticketRepository.findByReservation(reservation.idReservation)
    )
}
