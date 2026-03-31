package com.thrillathon.client.ui.orgainser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thrillathon.client.model.Organiser
import com.thrillathon.client.repository.OrganiserRepository

class organiserviewmodel : ViewModel() {

    private val _organizer = MutableLiveData<Organiser>()
    val organiser: LiveData<Organiser> = _organizer

    fun loadOrganizer() {
        _organizer.value = OrganiserRepository().getOrganiser()
    }
}
