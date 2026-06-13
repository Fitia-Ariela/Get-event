package datastore

import model.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.atomic.AtomicLong

@Serializable
data class StoreSnapshot(
    val users: List<User> = emptyList(),
    val locations: List<Location> = emptyList(),
    val events: List<Event> = emptyList(),
    val reservations: List<Reservation> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val tickets: List<Ticket> = emptyList(),
    val nextUserId: Long = 1,
    val nextLocationId: Long = 1,
    val nextEventId: Long = 1,
    val nextReservationId: Long = 1,
    val nextTransactionId: Long = 1,
    val nextTicketId: Long = 1
)

class MemoryStore(
    private val dataFile: File = File("data/store.json"),
    private val seedAdmin: Boolean = true
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val lock = Any()

    val users = mutableListOf<User>()
    val locations = mutableListOf<Location>()
    val events = mutableListOf<Event>()
    val reservations = mutableListOf<Reservation>()
    val transactions = mutableListOf<Transaction>()
    val tickets = mutableListOf<Ticket>()

    private val nextUserId = AtomicLong(1)
    private val nextLocationId = AtomicLong(1)
    private val nextEventId = AtomicLong(1)
    private val nextReservationId = AtomicLong(1)
    private val nextTransactionId = AtomicLong(1)
    private val nextTicketId = AtomicLong(1)

    init {
        load()
        if (seedAdmin) seedDefaultAdminIfEmpty()
    }

    fun nextUserId(): Long = nextUserId.getAndIncrement()
    fun nextLocationId(): Long = nextLocationId.getAndIncrement()
    fun nextEventId(): Long = nextEventId.getAndIncrement()
    fun nextReservationId(): Long = nextReservationId.getAndIncrement()
    fun nextTransactionId(): Long = nextTransactionId.getAndIncrement()
    fun nextTicketId(): Long = nextTicketId.getAndIncrement()

    fun <T> mutate(block: MemoryStore.() -> T): T = synchronized(lock) {
        block().also { persist() }
    }

    fun persist() {
        dataFile.parentFile?.mkdirs()
        val snapshot = StoreSnapshot(
            users = users.toList(),
            locations = locations.toList(),
            events = events.toList(),
            reservations = reservations.toList(),
            transactions = transactions.toList(),
            tickets = tickets.toList(),
            nextUserId = nextUserId.get(),
            nextLocationId = nextLocationId.get(),
            nextEventId = nextEventId.get(),
            nextReservationId = nextReservationId.get(),
            nextTransactionId = nextTransactionId.get(),
            nextTicketId = nextTicketId.get()
        )
        dataFile.writeText(json.encodeToString(snapshot))
    }

    private fun load() {
        if (!dataFile.exists()) return
        runCatching {
            val snapshot = json.decodeFromString<StoreSnapshot>(dataFile.readText())
            synchronized(lock) {
                users.clear()
                users.addAll(snapshot.users)
                locations.clear()
                locations.addAll(snapshot.locations)
                events.clear()
                events.addAll(snapshot.events)
                reservations.clear()
                reservations.addAll(snapshot.reservations)
                transactions.clear()
                transactions.addAll(snapshot.transactions)
                tickets.clear()
                tickets.addAll(snapshot.tickets)
                nextUserId.set(snapshot.nextUserId)
                nextLocationId.set(snapshot.nextLocationId)
                nextEventId.set(snapshot.nextEventId)
                nextReservationId.set(snapshot.nextReservationId)
                nextTransactionId.set(snapshot.nextTransactionId)
                nextTicketId.set(snapshot.nextTicketId)
            }
        }
    }

    private fun seedDefaultAdminIfEmpty() {
        synchronized(lock) {
            if (users.isNotEmpty()) return
            val admin = User(
                id = nextUserId.getAndIncrement(),
                numeroInscription = "ADMIN001",
                nom = "Administrateur",
                niveau = "N/A",
                parcours = "N/A",
                numeroTel = 0,
                email = "admin@getevent.local",
                nomFacebook = "admin",
                role = Role.ADMIN,
                passwordHash = util.PasswordHasher.hash("admin123")
            )
            users.add(admin)
            persist()
        }
    }
}
