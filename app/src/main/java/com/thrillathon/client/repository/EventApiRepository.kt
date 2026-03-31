package com.thrillathon.client.repository

import com.thrillathon.client.model.Event
import com.thrillathon.client.model.Seating
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class EventApiRepository(
    private val eventsUrl: String = DEFAULT_EVENTS_URL
) {

    fun getEvents(
        onSuccess: (List<Event>) -> Unit,
        onError: (String) -> Unit
    ) {
        thread {
            try {
                val url = URL(eventsUrl)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 15000
                    readTimeout = 15000
                    setRequestProperty("Accept", "application/json")
                }

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    onSuccess(fallbackEvents())
                    connection.disconnect()
                    return@thread
                }

                val response = connection.inputStream.bufferedReader().use(BufferedReader::readText)
                connection.disconnect()

                val parsedEvents = parseEventsResponse(response)
                onSuccess(parsedEvents.ifEmpty { fallbackEvents() })
            } catch (e: Exception) {
                onSuccess(fallbackEvents())
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
                    val seatItem = seatingsArray.optJSONObject(j) ?: continue
                    seatings += Seating(
                        seatType = seatItem.optString("seatType"),
                        price = seatItem.optDouble("price", 0.0),
                        totalSeats = seatItem.optInt("totalSeats", 0),
                        seatsSold = seatItem.optInt("seatsSold", 0),
                        isActive = seatItem.optBoolean("isActive", true)
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

    private fun fallbackEvents(): List<Event> {
        val defaultSeatings = listOf(
            Seating("General", 1.0, 500, 9, true),
            Seating("VIP", 0.0, 100, 16, true)
        )
        return listOf(
            Event(
                id = "694291bb1e613c43e1b18a76",
                eventName = "Test Update",
                date = "2026-02-25",
                status = "completed",
                description = "Experience the thrill of live music and high-energy performances as the stage lights up with sound, rhythm, and excitement. From crowd-pumping tracks to feel-good beats, this event is all about music, fun, and creating memories with fellow music lovers in your town.",
                location = "Delhi Grounds",
                seatings = defaultSeatings
            ),
            Event(
                id = "694291bb1e613c43e1b18a71",
                eventName = "daksh Music Festival 2024",
                date = "2026-02-24",
                status = "completed",
                description = "Experience the thrill of live music and high-energy performances as the stage lights up with sound, rhythm, and excitement. From crowd-pumping tracks to feel-good beats, this event is all about music, fun, and creating memories with fellow music lovers in your town.",
                location = "hii Delhi Grounds",
                seatings = defaultSeatings
            ),
            Event(
                id = "69a5bb7cd4f604c550e4123a",
                eventName = "dfvdfdfbpunnet",
                date = "2026-03-26",
                status = "completed",
                description = "bdfhj dfvhhjbdfjh hhjbnjhndfn jbnjnfd jknjndf jknjkndfgv kj",
                location = "hihihihihihihi",
                seatings = listOf(Seating("vip", 1.0, 2, 0, true), Seating("general", 2.0, 2, 0, true))
            ),
            Event(
                id = "69a5bd94b554abb7ff6af815",
                eventName = "rtdjcfgnv b",
                date = "2026-03-16",
                status = "completed",
                description = "ghcvmh bhmgvb nmghv hbv bv. bn ",
                location = "hihihihihihihi",
                seatings = listOf(Seating("general", 0.01, 1, 0, true))
            ),
            Event(
                id = "69a5fc339e52c94da0694c95",
                eventName = "Punnet",
                date = "2026-03-11",
                status = "completed",
                description = "fdvdf fgfbg. fgb fg fg. bfgghfbf gbfbgf fgbfgfb gffbg ",
                location = "hihihihihihihi",
                seatings = listOf(Seating("general", 0.0, 1, 0, true))
            )
        )
    }

    companion object {
        private const val DEFAULT_EVENTS_URL = "https://your-domain.com/api/events"
    }
}
