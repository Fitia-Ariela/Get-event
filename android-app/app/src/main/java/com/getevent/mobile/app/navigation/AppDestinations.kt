package com.getevent.mobile.app.navigation

object AppDestinations {
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val STUDENT_HOME = "student_home"
    const val EVENT_DETAIL = "event_detail/{eventId}"
    const val EVENT_DETAIL_BASE = "event_detail"
    const val MY_RESERVATIONS = "my_reservations"
    const val MY_TICKET_QR = "my_ticket_qr/{reservationId}"
    const val MY_TICKET_QR_BASE = "my_ticket_qr"
    const val PROFILE = "profile"

    const val BOARD_DASHBOARD = "board_dashboard"
    const val QR_SCANNER = "qr_scanner"
    const val RESERVATION_LIST = "reservation_list"

    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val CRUD_EVENT = "crud_event"
    const val CRUD_LOCATION = "crud_location"
    const val MANAGE_USERS = "manage_users"
    const val STATISTICS = "statistics"
}
