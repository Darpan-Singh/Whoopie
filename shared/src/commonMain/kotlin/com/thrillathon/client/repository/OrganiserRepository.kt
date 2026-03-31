package com.thrillathon.client.repository

import com.thrillathon.client.model.Organiser

class OrganiserRepository {
    fun getOrganiser(): Organiser = Organiser(
        name = "Updated Tech Events Co",
        email = "contact@techevents.co",
        phone = "+1-555-0123",
        address = "123 Tech Street, Silicon Valley, CA",
        website = "https://techevents.co",
        description = "Leading technology and innovation event organizer",
        contactPerson = "Nishant",
        status = "active",
        totalRevenue = 0,
        totalEvents = 3,
        activeEvents = 2,
        joinDate = "2025-11-11"
    )
}
