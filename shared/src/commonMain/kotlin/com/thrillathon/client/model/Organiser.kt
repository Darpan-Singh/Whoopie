package com.thrillathon.client.model

data class Organiser(
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val website: String,
    val description: String,
    val contactPerson: String,
    val status: String,
    val totalRevenue: Int,
    val totalEvents: Int,
    val activeEvents: Int,
    val joinDate: String
)
