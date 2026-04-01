package com.thrillathon.client.ui.organiser

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.thrillathon.client.model.Organiser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class OrganiserViewModel(application: Application) : AndroidViewModel(application) {

    private val _organizer = MutableLiveData<Organiser>()
    val organiser: LiveData<Organiser> = _organizer

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val client = OkHttpClient()
    private val BASE_URL = "https://backendmongo-tau.vercel.app"
    private val PROFILE_ENDPOINT = "/api/organizers/auth/profile"

    fun loadOrganizer() {
        val prefs = getApplication<Application>()
            .getSharedPreferences("whoopie_prefs", 0)
        val token = prefs.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            _error.value = "Not authenticated"
            return
        }

        _loading.value = true
        viewModelScope.launch {
            try {
                val organiser = fetchProfile(token)
                if (organiser != null) {
                    _organizer.value = organiser
                } else {
                    _error.value = "Failed to load profile"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun fetchProfile(token: String): Organiser? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$BASE_URL$PROFILE_ENDPOINT")
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val body = response.body?.string() ?: return@withContext null
            val json = JSONObject(body)
            val org = json.optJSONObject("data")?.optJSONObject("organizer")
                ?: return@withContext null

            Organiser(
                name = org.optString("name"),
                email = org.optString("email"),
                phone = org.optString("phone"),
                address = org.optString("address"),
                website = org.optString("website"),
                description = org.optString("description"),
                contactPerson = org.optString("contactPerson"),
                status = org.optString("status", "active"),
                totalRevenue = org.optInt("totalRevenue", 0),
                totalEvents = org.optInt("totalEvents", 0),
                activeEvents = org.optInt("activeEvents", 0),
                joinDate = org.optString("joinDate")
            )
        }
    }
}
