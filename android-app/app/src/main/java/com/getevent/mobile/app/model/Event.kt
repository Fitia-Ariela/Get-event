package com.getevent.mobile.app.model

data class Event(
    val idEvenement: Long = 0,
    val nomEvenement: String,
    val dateEvenement: String,
    val lieuId: Long,
    val description: String,
    val estPrive: Boolean = false,
    val tarif: Double = 0.0
)
