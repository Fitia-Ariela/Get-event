package com.getevent.mobile.app.model

data class Location(
    val id: Long = 0,
    val nom: String,
    val longitude: Float,
    val latitude: Float,
    val capacite: Int
)
