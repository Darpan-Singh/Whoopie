package com.thrillathon.client.model

data class Event(
    val id: String,
    val eventName: String,
    val date: String,
    val status: String,
    val description: String,
    val location: String = "",
    val seatings: List<Seating> = emptyList()
)

data class Seating(
    val seatType: String,
    val price: Double,
    val totalSeats: Int,
    val lockedSeats: Int = 0,
    val seatsSold: Int,
    val isActive: Boolean
)
