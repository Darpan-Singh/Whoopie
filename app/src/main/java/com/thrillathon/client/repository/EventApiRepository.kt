package com.thrillathon.client.repository

import com.thrillathon.client.model.Event
import com.thrillathon.client.model.Seating
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.concurrent.thread

class EventApiRepository(private val token: String) {

    private val client = OkHttpClient()
    private val EVENTS_URL = "https://backendmongo-tau.vercel.app/api/organizers/auth/events"

    fun getEvents(
        onSuccess: (List<Event>) -> Unit,
        onError: (String) -> Unit
    ) {
        thread {
            try {
                val request = Request.Builder()
                    .url(EVENTS_URL)
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    onError("Server error: ${response.code}")
                    return@thread
                }

                val body = response.body?.string()
                if (body.isNullOrEmpty()) {
                    onError("Empty response")
                    return@thread
                }

                val events = parseEventsResponse(body)
                onSuccess(events)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    private fun parseEventsResponse(json: String): List<Event> {
        val root = JSONObject(json)
        val data = root.optJSONObject("data") ?: return emptyList()
        val eventsArray = data.optJSONArray("events") ?: return emptyList()

        val events = mutableListOf<Event>()
        for (i in 0 until eventsArray.length()) {
            val item = eventsArray.optJSONObject(i) ?: continue

            val id = item.optString("id").ifBlank { item.optString("_id") }
            val name = item.optString("name")
            val date = formatDate(item.optString("date"))
            val status = item.optString("status")
            val description = item.optString("description")
            val location = item.optString("location")

            val seatings = mutableListOf<Seating>()
            val seatingsArray = item.optJSONArray("seatings")
            if (seatingsArray != null) {
                for (j in 0 until seatingsArray.length()) {
                    val s = seatingsArray.optJSONObject(j) ?: continue
                    seatings += Seating(
                        seatType = s.optString("seatType"),
                        price = s.optDouble("price", 0.0),
                        totalSeats = s.optInt("totalSeats", 0),
                        lockedSeats = s.optInt("lockedSeats", 0),
                        seatsSold = s.optInt("seatsSold", 0),
                        isActive = s.optBoolean("isActive", true)
                    )
                }
            }

            events += Event(
                id = id,
                eventName = name,
                date = date,
                status = status,
                description = description,
                location = location,
                seatings = seatings
            )
        }
        return events
    }

    private fun formatDate(rawDate: String): String {
        return if (rawDate.length >= 10) rawDate.substring(0, 10) else rawDate
    }
}
