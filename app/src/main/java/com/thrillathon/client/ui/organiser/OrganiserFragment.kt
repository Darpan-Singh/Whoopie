package com.thrillathon.client.ui.organiser

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.thrillathon.client.R
import com.thrillathon.client.model.Organiser

class OrganiserFragment : Fragment() {

    private val viewModel: OrganiserViewModel by viewModels()

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvWebsite: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvEvents: TextView
    private lateinit var tvActiveEvents: TextView
    private lateinit var tvRevenue: TextView
    private lateinit var tvStatus: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_organiser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Bind Views
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvPhone = view.findViewById(R.id.tvPhone)
        tvAddress = view.findViewById(R.id.tvAddress)
        tvWebsite = view.findViewById(R.id.tvWebsite)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvEvents = view.findViewById(R.id.tvEvents)
        tvActiveEvents = view.findViewById(R.id.tvActiveEvents)
        tvRevenue = view.findViewById(R.id.tvRevenue)
        tvStatus = view.findViewById(R.id.tvStatus)

        // 🔥 Observe data
        viewModel.organiser.observe(viewLifecycleOwner) {
            bindData(it)
        }

        // 🔥 Load data
        viewModel.loadOrganizer()
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(org: Organiser) {
        tvName.text = org.name
        tvEmail.text = org.email
        tvPhone.text = org.phone
        tvAddress.text = org.address
        tvWebsite.text = org.website
        tvDescription.text = org.description

        tvEvents.text = "${org.totalEvents}"
        tvActiveEvents.text = "${org.activeEvents}"
        tvRevenue.text = if (org.totalRevenue > 0) "₹${org.totalRevenue}" else "₹0"

        tvStatus.text = org.status.uppercase()
    }
}
