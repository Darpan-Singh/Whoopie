package com.thrillathon.client.model

data class Event(
    val id: String,
    val eventName: String,
    val date: String,
    val status: String,
    val description: String,
    val location: String = ""
)
