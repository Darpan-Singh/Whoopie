package com.thrillathon.client.ui.organiser

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thrillathon.client.R
import com.thrillathon.client.model.Event
import com.thrillathon.client.repository.EventApiRepository
import org.json.JSONArray
import org.json.JSONObject

class EventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_event, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.eventRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val openEventDetails: (Event) -> Unit = { event -> openEventDetail(event) }
        recyclerView.adapter = EventAdapter(emptyList(), openEventDetails)

        val prefs = requireContext().getSharedPreferences("whoopie_prefs", 0)
        val token = prefs.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Not authenticated", Toast.LENGTH_SHORT).show()
            return view
        }

        EventApiRepository(token).getEvents(
            onSuccess = { eventList ->
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        recyclerView.adapter = EventAdapter(eventList, openEventDetails)
                    }
                }
            },
            onError = { message ->
                if (isAdded) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to load events: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        return view
    }

    private fun openEventDetail(event: Event) {
        val intent = Intent(requireContext(), EventDetailActivity::class.java)
        intent.putExtra("event_id", event.id)
        intent.putExtra("name", event.eventName)
        intent.putExtra("date", event.date)
        intent.putExtra("status", event.status)
        intent.putExtra("description", event.description)
        intent.putExtra("location", event.location)

        val seatingsArray = JSONArray()
        event.seatings.forEach { seating ->
            val seatingObj = JSONObject().apply {
                put("seatType", seating.seatType)
                put("price", seating.price)
                put("totalSeats", seating.totalSeats)
                put("lockedSeats", seating.lockedSeats)
                put("seatsSold", seating.seatsSold)
                put("isActive", seating.isActive)
            }
            seatingsArray.put(seatingObj)
        }
        intent.putExtra("seatings_json", seatingsArray.toString())

        startActivity(intent)
    }
}
