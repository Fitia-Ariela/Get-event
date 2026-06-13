package di

import controller.*
import datastore.MemoryStore
import repository.event.EventRepository
import repository.event.LocationRepository
import repository.reservation.ReservationRepository
import repository.ticket.TicketRepository
import repository.transaction.TransactionRepository
import repository.user.UserRepository
import service.*
import util.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import java.io.File

fun Application.configureDependencyInjection() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val storePath = runCatching { environment.config.property("data.store.path").getString() }.getOrNull()
    val seedAdmin = runCatching {
        environment.config.property("data.seedAdmin").getString().toBoolean()
    }.getOrDefault(true)

    dependencies {
        provide {
            MemoryStore(
                dataFile = File(storePath ?: "data/store.json"),
                seedAdmin = seedAdmin
            )
        }
        provide {
            JwtConfig(
                secret = jwtSecret,
                issuer = jwtIssuer,
                audience = jwtAudience,
                realm = jwtRealm
            )
        }

        provide { UserRepository(resolve()) }
        provide { LocationRepository(resolve()) }
        provide { EventRepository(resolve()) }
        provide { ReservationRepository(resolve()) }
        provide { TransactionRepository(resolve()) }
        provide { TicketRepository(resolve()) }

        provide { TicketService(resolve(), resolve()) }
        provide { EventService(resolve(), resolve(), resolve(), resolve()) }
        provide { UserService(resolve(), resolve()) }
        provide { AuthService(resolve(), resolve(), resolve()) }
        provide { ReservationService(resolve(), resolve(), resolve(), resolve(), resolve(), resolve(), resolve()) }
        provide { TransactionService(resolve(), resolve(), resolve(), resolve(), resolve()) }

        provide { AuthController(resolve(), resolve()) }
        provide { UserController(resolve()) }
        provide { EventController(resolve()) }
        provide { ReservationController(resolve()) }
        provide { TicketController(resolve(), resolve()) }
        provide { TransactionController(resolve()) }
    }
}
