package com.thrillathon.client

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val BASE_URL = "https://backendmongo-tau.vercel.app"
    private val LOGIN_ENDPOINT = "/api/organizers/auth/login"
    private val PROFILE_ENDPOINT = "/api/organizers/auth/profile"
    private val client = OkHttpClient()
    private val TAG = "LoginActivity"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("whoopie_prefs", MODE_PRIVATE)

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvError = findViewById<TextView>(R.id.tvError)

        btnLogin.setOnClickListener {
            val email = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                tvError.text = "Email and password cannot be empty"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            tvError.visibility = View.GONE
            Log.d(TAG, "Login attempt with email: $email")
            performLogin(email, password, tvError, btnLogin)
        }
    }

    private fun performLogin(email: String, password: String, tvError: TextView, btnLogin: Button) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = loginWithAPI(email, password)
                Log.d(TAG, "Login result - Token received: ${token != null}")
                
                if (token != null) {
                    // Store token in SharedPreferences
                    sharedPreferences.edit().putString("auth_token", token).apply()
                    Log.d(TAG, "Token stored in SharedPreferences")
                    
                    // Fetch organizer profile
                    val organizer = fetchOrganizerProfile(token)
                    if (organizer != null) {
                        // Store organizer details
                        sharedPreferences.edit().apply {
                            putString("organizer_id", organizer.optString("_id"))
                            putString("organizer_name", organizer.optString("name"))
                            putString("organizer_email", organizer.optString("email"))
                            putString("organizer_phone", organizer.optString("phone"))
                            putString("organizer_address", organizer.optString("address"))
                            putString("organizer_logo", organizer.optString("logo"))
                        }.apply()
                        Log.d(TAG, "Organizer details stored")
                        
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        tvError.text = "Failed to fetch organizer details"
                        tvError.visibility = View.VISIBLE
                        btnLogin.isEnabled = true
                    }
                } else {
                    tvError.text = "Invalid email or password"
                    tvError.visibility = View.VISIBLE
                    btnLogin.isEnabled = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login exception: ${e.message}", e)
                tvError.text = "Login failed: ${e.message}"
                tvError.visibility = View.VISIBLE
                btnLogin.isEnabled = true
            }
        }
    }

    private suspend fun loginWithAPI(email: String, password: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }.toString()

                Log.d(TAG, "Request URL: $BASE_URL$LOGIN_ENDPOINT")
                Log.d(TAG, "Request Body: $requestBody")

                val body = RequestBody.create("application/json".toMediaType(), requestBody)
                val request = Request.Builder()
                    .url("$BASE_URL$LOGIN_ENDPOINT")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                Log.d(TAG, "Response Code: ${response.code}")
                
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Response Body: $responseBody")
                    
                    val jsonResponse = JSONObject(responseBody ?: "")
                    val token = jsonResponse.optString("token")
                    Log.d(TAG, "Token extracted: $token")
                    return@withContext token
                } else {
                    Log.e(TAG, "Login failed with code: ${response.code}")
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e(TAG, "API Error: ${e.message}", e)
                null
            }
        }
    }

    private suspend fun fetchOrganizerProfile(token: String): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching organizer profile with token")
                
                val request = Request.Builder()
                    .url("$BASE_URL$PROFILE_ENDPOINT")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                Log.d(TAG, "Profile Response Code: ${response.code}")
                
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d(TAG, "Profile Response Body: $responseBody")
                    
                    val jsonResponse = JSONObject(responseBody ?: "")
                    val organizerData = jsonResponse.optJSONObject("data")?.optJSONObject("organizer")
                    Log.d(TAG, "Organizer data extracted: ${organizerData?.optString("name")}")
                    return@withContext organizerData
                } else {
                    Log.e(TAG, "Profile fetch failed with code: ${response.code}")
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Profile Fetch Error: ${e.message}", e)
                null
            }
        }
    }
}