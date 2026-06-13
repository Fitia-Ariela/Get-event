package routes

import controller.ReservationController
import controller.TransactionController
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.reservationRoutes(
    reservationController: ReservationController,
    transactionController: TransactionController
) {
    route("/api/reservations") {
        authenticate("auth-jwt") {
            get("/me") { reservationController.listMine(call) }
            get { reservationController.listAll(call) }
            get("/{id}") { reservationController.get(call, call.parameters["id"]!!.toLong()) }
            post { reservationController.create(call) }
            delete("/{id}") { reservationController.cancel(call, call.parameters["id"]!!.toLong()) }
        }
    }
    route("/api/transactions") {
        authenticate("auth-jwt") {
            post("/pay") { transactionController.pay(call) }
            get("/reservation/{reservationId}") {
                transactionController.getByReservation(
                    call,
                    call.parameters["reservationId"]!!.toLong()
                )
            }
        }
    }
}
