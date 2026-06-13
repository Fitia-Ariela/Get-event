import datastore.MemoryStore
import dto.EventRequest
import dto.LocationRequest
import dto.RegisterRequest
import model.Role
import repository.event.EventRepository
import repository.event.LocationRepository
import repository.reservation.ReservationRepository
import repository.ticket.TicketRepository
import repository.transaction.TransactionRepository
import repository.user.UserRepository
import service.AuthService
import service.EventService
import service.ReservationService
import service.TicketService
import service.TransactionService
import service.UserService
import util.JwtConfig
import java.io.File
import java.util.UUID

object TestFixtures {

    val jwtConfig = JwtConfig(
        secret = "test-secret-key-for-unit-tests-only!!",
        issuer = "getevent",
        audience = "getevent-api",
        realm = "GetEvent"
    )

    fun isolatedStore(): MemoryStore {
        val file = File.createTempFile("getevent-test-", ".json")
        file.deleteOnExit()
        return MemoryStore(dataFile = file, seedAdmin = false)
    }

    fun testConfig(storeFile: File? = null): io.ktor.server.config.MapApplicationConfig {
        val pairs = mutableListOf<Pair<String, String>>(
            "jwt.secret" to "test-secret-key-for-unit-tests-only!!",
            "jwt.issuer" to "getevent",
            "jwt.audience" to "getevent-api",
            "jwt.realm" to "GetEvent",
            "data.seedAdmin" to "false"
        )
        storeFile?.let { pairs.add("data.store.path" to it.absolutePath) }
        return io.ktor.server.config.MapApplicationConfig(*pairs.toTypedArray())
    }

    data class Services(
        val store: MemoryStore,
        val userRepository: UserRepository,
        val locationRepository: LocationRepository,
        val eventRepository: EventRepository,
        val reservationRepository: ReservationRepository,
        val transactionRepository: TransactionRepository,
        val ticketRepository: TicketRepository,
        val userService: UserService,
        val authService: AuthService,
        val ticketService: TicketService,
        val eventService: EventService,
        val reservationService: ReservationService,
        val transactionService: TransactionService
    )

    fun createServices(store: MemoryStore = isolatedStore()): Services {
        val userRepository = UserRepository(store)
        val locationRepository = LocationRepository(store)
        val eventRepository = EventRepository(store)
        val reservationRepository = ReservationRepository(store)
        val transactionRepository = TransactionRepository(store)
        val ticketRepository = TicketRepository(store)

        val userService = UserService(userRepository, store)
        val authService = AuthService(userRepository, userService, jwtConfig)
        val ticketService = TicketService(ticketRepository, store)
        val eventService = EventService(
            eventRepository,
            locationRepository,
            reservationRepository,
            store
        )
        val reservationService = ReservationService(
            reservationRepository,
            eventRepository,
            ticketRepository,
            transactionRepository,
            eventService,
            ticketService,
            store
        )
        val transactionService = TransactionService(
            transactionRepository,
            reservationRepository,
            eventRepository,
            ticketService,
            store
        )

        return Services(
            store = store,
            userRepository = userRepository,
            locationRepository = locationRepository,
            eventRepository = eventRepository,
            reservationRepository = reservationRepository,
            transactionRepository = transactionRepository,
            ticketRepository = ticketRepository,
            userService = userService,
            authService = authService,
            ticketService = ticketService,
            eventService = eventService,
            reservationService = reservationService,
            transactionService = transactionService
        )
    }

    fun uniqueEmail(): String = "user-${UUID.randomUUID()}@test.local"

    fun studentRequest(email: String = uniqueEmail()) = RegisterRequest(
        numeroInscription = "ETU${UUID.randomUUID().toString().take(8)}",
        nom = "Etudiant Test",
        niveau = "L3",
        parcours = "Telecom",
        numeroTel = 340000000L,
        email = email,
        nomFacebook = "fb_test",
        password = "password123",
        role = Role.STUDENT
    )

    fun locationRequest() = LocationRequest(
        nom = "Amphi A",
        longitude = 47.51f,
        latitude = -18.91f,
        capacite = 50
    )

    fun publicEventRequest(lieuId: Long) = EventRequest(
        nomEvenement = "Conférence publique",
        dateEvenement = "2026-06-15T10:00:00Z",
        lieuId = lieuId,
        description = "Événement ouvert à tous",
        estPrive = false,
        tarif = 0.0
    )

    fun privateEventRequest(lieuId: Long, tarif: Double = 5000.0) = EventRequest(
        nomEvenement = "Gala privé",
        dateEvenement = "2026-06-20T18:00:00Z",
        lieuId = lieuId,
        description = "Événement payant",
        estPrive = true,
        tarif = tarif
    )

    fun seedAdmin(store: MemoryStore, email: String = "admin@test.local"): model.User {
        val admin = model.User(
            id = store.nextUserId(),
            numeroInscription = "ADM001",
            nom = "Admin Test",
            niveau = "N/A",
            parcours = "N/A",
            numeroTel = 0,
            email = email,
            nomFacebook = "admin",
            role = Role.ADMIN,
            passwordHash = util.PasswordHasher.hash("admin123")
        )
        return UserRepository(store).save(admin)
    }
}
