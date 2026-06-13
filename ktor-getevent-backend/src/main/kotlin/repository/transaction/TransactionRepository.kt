package repository.transaction

import datastore.MemoryStore
import model.Transaction

class TransactionRepository(private val store: MemoryStore) {

    fun findAll(): List<Transaction> = store.transactions.toList()

    fun findById(id: Long): Transaction? = store.transactions.find { it.idTransaction == id }

    fun findByReservation(reservationId: Long): Transaction? =
        store.transactions.find { it.reservationId == reservationId }

    fun save(transaction: Transaction): Transaction = store.mutate {
        val index = transactions.indexOfFirst { it.idTransaction == transaction.idTransaction }
        if (index >= 0) transactions[index] = transaction else transactions.add(transaction)
        transaction
    }
}
