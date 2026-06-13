import controller.*
import di.configureDependencyInjection
import dto.ErrorResponse
import routes.*
import security.configureSecurity
import util.JwtConfig
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.di.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDependencyInjection()
    val jwtConfig: JwtConfig by dependencies
    configureSecurity(jwtConfig)
    configureStatusPages()
    configureMonitoring()
    configureRouting()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.Accept)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Requête invalide"))
        }
        exception<IllegalAccessException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, ErrorResponse(cause.message ?: "Accès refusé"))
        }
        exception<NoSuchElementException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Ressource introuvable"))
        }
        exception<IllegalStateException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, ErrorResponse(cause.message ?: "Conflit"))
        }
    }
}

fun Application.configureRouting() {
    val authController: AuthController by dependencies
    val userController: UserController by dependencies
    val eventController: EventController by dependencies
    val reservationController: ReservationController by dependencies
    val ticketController: TicketController by dependencies
    val transactionController: TransactionController by dependencies

    routing {
        get("/") {
            call.respond(
                mapOf(
                    "app" to "GetEvent API",
                    "version" to "1.0.0",
                    "docs" to "/api"
                )
            )
        }
        get("/api") {
            call.respond(
                mapOf(
                    "auth" to listOf("POST /api/auth/login", "POST /api/auth/register", "GET /api/auth/me"),
                    "events" to listOf("GET /api/events", "POST /api/events (ADMIN)"),
                    "reservations" to listOf("POST /api/reservations", "GET /api/reservations/me"),
                    "payments" to listOf("POST /api/transactions/pay")
                )
            )
        }
        authRoutes(authController)
        userRoutes(userController)
        eventRoutes(eventController)
        reservationRoutes(reservationController, transactionController)
        ticketRoutes(ticketController)
    }
}
