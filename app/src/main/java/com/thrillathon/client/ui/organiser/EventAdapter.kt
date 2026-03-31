package com.thrillathon.client.ui.organiser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thrillathon.client.R
import com.thrillathon.client.model.Event

class EventAdapter(
    private val list: List<Event>,
    private val onClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private val posterDrawables = listOf(
        R.drawable.poster_blue,
        R.drawable.poster_purple,
        R.drawable.poster_teal
    )

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posterBg: View = itemView.findViewById(R.id.eventPoster)
        val name: TextView = itemView.findViewById(R.id.eventName)
        val location: TextView = itemView.findViewById(R.id.posterLocation)
        val date: TextView = itemView.findViewById(R.id.eventDate)
        val status: TextView = itemView.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = list[position]
        holder.posterBg.setBackgroundResource(posterDrawables[position % posterDrawables.size])
        holder.name.text = event.eventName
        holder.location.text = event.location
        holder.date.text = event.date
        holder.status.text = event.status
        holder.itemView.setOnClickListener { onClick(event) }
    }

    override fun getItemCount(): Int = list.size
}
