package com.thrillathon.client.repository

import com.thrillathon.client.model.Event

class EventRepository {
    fun getEvents(): List<Event> = listOf(
        Event(
            id = "1",
            eventName = "Thrillathon 2026",
            date = "27 March 2026",
            status = "Active",
            description = "India's biggest tech + event fest",
            location = "Mumbai"
        ),
        Event(
            id = "2",
            eventName = "Music Night",
            date = "5 April 2026",
            status = "Active",
            description = "Live DJ + Concert",
            location = "Bangalore"
        ),
        Event(
            id = "3",
            eventName = "Startup Expo",
            date = "12 April 2026",
            status = "Upcoming",
            description = "Startup showcase event",
            location = "Delhi"
        )
    )
}
