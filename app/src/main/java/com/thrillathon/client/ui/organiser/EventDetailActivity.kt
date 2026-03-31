package com.thrillathon.client.ui.organiser

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.thrillathon.client.R
import com.thrillathon.client.model.Seating
import org.json.JSONArray

class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventdetail)

        val name = intent.getStringExtra("name")
        val date = intent.getStringExtra("date")
        val status = intent.getStringExtra("status")
        val description = intent.getStringExtra("description")
        val location = intent.getStringExtra("location")
        val seatingsJson = intent.getStringExtra("seatings_json")

        findViewById<TextView>(R.id.eventName).text = name
        findViewById<TextView>(R.id.eventDate).text = date
        findViewById<TextView>(R.id.status).text = status
        findViewById<TextView>(R.id.description).text = description
        findViewById<TextView>(R.id.location).text = location

        val seatingContainer = findViewById<LinearLayout>(R.id.seatingContainer)
        if (seatingsJson != null) {
            val seatings = parseSeatings(seatingsJson)
            displaySeatings(seatingContainer, seatings)
        }
    }

    private fun parseSeatings(json: String): List<Seating> {
        val list = mutableListOf<Seating>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Seating(
                        seatType = obj.getString("seatType"),
                        price = obj.getDouble("price"),
                        totalSeats = obj.getInt("totalSeats"),
                        seatsSold = obj.getInt("seatsSold"),
                        isActive = obj.getBoolean("isActive")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    @SuppressLint("SetTextI18n")
    private fun displaySeatings(container: LinearLayout, seatings: List<Seating>) {
        container.removeAllViews()
        if (seatings.isEmpty()) {
            val tv = TextView(this).apply {
                text = "No seating information available"
                setTextColor(ContextCompat.getColor(context, R.color.whoopie_text_secondary))
                textSize = 14f
            }
            container.addView(tv)
            return
        }

        for (seating in seatings) {
            val view = layoutInflater.inflate(R.layout.item_seating, container, false)
            
            val tvType = view.findViewById<TextView>(R.id.tvSeatType)
            val tvPrice = view.findViewById<TextView>(R.id.tvPrice)
            val tvAvailability = view.findViewById<TextView>(R.id.tvAvailability)
            val tvPercent = view.findViewById<TextView>(R.id.tvPercent)
            val progress = view.findViewById<LinearProgressIndicator>(R.id.pbAvailability)

            tvType.text = seating.seatType.replaceFirstChar { it.uppercase() }
            tvPrice.text = if (seating.price <= 0) "FREE" else "₹${seating.price}"
            
            val sold = seating.seatsSold
            val total = seating.totalSeats
            tvAvailability.text = "$sold / $total seats sold"
            
            val percentage = if (total > 0) (sold * 100 / total) else 0
            tvPercent.text = "$percentage%"
            progress.progress = percentage

            container.addView(view)
        }
    }
}
