package model

import kotlinx.serialization.Serializable

@Serializable
enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED
}

@Serializable
data class Transaction(
    val idTransaction: Long = 0,
    val montant: Double,
    val dateTransaction: String,
    val modePaiement: String,
    val statutPaiement: PaymentStatus,
    val referencePaiement: String,
    val reservationId: Long
)
