package com.thrillathon.client.ui.orgainser

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thrillathon.client.R
import com.thrillathon.client.ui.dashboard.DashboardFragment

class EventDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventdetail)

        val name = intent.getStringExtra("name")
        val date = intent.getStringExtra("date")
        val status = intent.getStringExtra("status")
        val description = intent.getStringExtra("description")
        val location = intent.getStringExtra("location")
        val button = findViewById<Button>(R.id.button)


        findViewById<TextView>(R.id.eventName).text = name
        findViewById<TextView>(R.id.eventDate).text = date
        findViewById<TextView>(R.id.status).text = status
        findViewById<TextView>(R.id.description).text = description
        findViewById<TextView>(R.id.location).text = location


    }
}