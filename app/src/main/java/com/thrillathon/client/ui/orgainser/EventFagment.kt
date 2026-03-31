package com.thrillathon.client.ui.orgainser

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thrillathon.client.R
import com.thrillathon.client.repository.EventRepository

class EventFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_event, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.eventRecycler)
        val eventList = EventRepository().getEvents()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = EventAdapter(eventList) { event ->
            val intent = Intent(requireContext(), EventDetailActivity::class.java)
            intent.putExtra("name", event.eventName)
            intent.putExtra("date", event.date)
            intent.putExtra("status", event.status)
            intent.putExtra("description", event.description)
            intent.putExtra("location", event.location)
            startActivity(intent)
        }

        return view
    }
}
