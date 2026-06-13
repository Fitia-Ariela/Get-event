package com.getevent.mobile.app.model

data class Ticket(
    val idTicket: Long = 0,
    val urlCode: String,
    val dateGeneration: String,
    val estUtilise: Boolean = false,
    val reservationId: Long
)
