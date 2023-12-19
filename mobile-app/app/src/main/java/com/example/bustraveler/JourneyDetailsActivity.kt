/**
 * File: JourneyDetailsActivity.kt
 * Description: Activity to display journey details, including the last journey fare, available credit, and a list of past journeys.
 * Author: IT21358234_Perera K.A.S.N.
 * Version: 1.0
 * Date: October 15, 2023
 */
package com.example.bustraveler

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bustraveler.adapters.JourneyAdapter
import com.example.bustraveler.dataclasses.Journey
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class JourneyDetailsActivity : AppCompatActivity() {
    private lateinit var textViewLastJourneyFare: TextView
    private lateinit var textViewAvailableCredit: TextView

    private lateinit var recyclerViewJourneys: RecyclerView

    private val journeyList = ArrayList<Journey>()
    private lateinit var journeyAdapter: JourneyAdapter


    // Define a list for journeys

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey_details)

        supportActionBar?.hide()

        textViewLastJourneyFare = findViewById(R.id.tvLastJourneyFare)
        textViewAvailableCredit = findViewById(R.id.tvCredits)

        recyclerViewJourneys = findViewById(R.id.recyclerViewJourneys)
        journeyAdapter = JourneyAdapter(journeyList)
        recyclerViewJourneys.adapter = journeyAdapter
        recyclerViewJourneys.layoutManager = LinearLayoutManager(this)

        // Retrieve and display the last journey fare and available credit
        retrieveLastJourneyFareAndCredit()

        // Retrieve and display the list of journeys in the RecyclerView
        retrieveJourneys()
    }

    /**
     * Implement code to make a GET request to your API to retrieve last journey fare and available credit.
     * Update textViewLastJourneyFare and textViewAvailableCredit with the retrieved values.
     */
    private fun retrieveLastJourneyFareAndCredit() {
        // Get the user's ID from shared preferences(local storage)
        val packageName = applicationContext.packageName
        val preferencesName = "$packageName.user_preferences"
        val sharedPreferences = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId","").toString()

        // Create an OkHttpClient instance to make HTTP requests
        val client = OkHttpClientSingleton.getInstance()

        // Define the URL for fetching user details from the server
        val url = "${ApiUrlSingleton.instance.getUserEndpoint}/$userId"

        // Create an HTTP GET request to fetch user details
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        // Send the request and handle the response using a callback
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseData = response.body?.string()
                        val jsonResponse = JSONObject(responseData)
                        val success = jsonResponse.getBoolean("success")

                        runOnUiThread {
                            if (success) {
                                val data = jsonResponse.getJSONObject("data")

                                var credit = 0.0
                                var lastJourneyFare = 0.0
                                if(data.has("credit")
                                    && data.has("lastJourneyFare")){
                                    credit = data.getDouble("credit")
                                    lastJourneyFare = data.getDouble("lastJourneyFare")
                                }else{
                                    showToast("Empty data found")
                                }


                                // Update TextViews with user details
                                val lastJourneyFareText = "Rs. $lastJourneyFare"
                                val creditText = "$credit"
                                textViewAvailableCredit.text = creditText
                                textViewLastJourneyFare.text = lastJourneyFareText
                            } else {
                                showToast("Failed to retrieve user details")
                            }
                        }
                        //exception handling
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                showToast("Failed to retrieve user details")
            }
        })

    }

    /**
     * Retrieve a list of past journeys from the API and update the RecyclerView with the data.
     */
    private fun retrieveJourneys() {
        // Get the user's ID from shared preferences(local storage)
        val packageName = applicationContext.packageName
        val preferencesName = "$packageName.user_preferences"
        val sharedPreferences = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId","").toString()

        // Define the API endpoint for retrieving user journeys
        val apiUrl = "${ApiUrlSingleton.instance.getJourneyEndpoint}/$userId"

        // Create an OkHttpClient instance to make an HTTP GET request
        val client = OkHttpClientSingleton.getInstance()
        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .build()

        // Execute the HTTP request and handle the response using a callback
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Parse the response data and populate the journey list
                    val responseData = response.body?.string()
                    try {
                        val jsonResponse  = JSONObject(responseData)
                        val journeysArray = jsonResponse.getJSONArray("journeys")
                        for (i in 0 until journeysArray.length()) {
                            val journeyObject = journeysArray.getJSONObject(i)
                            val startLocation = journeyObject.getString("startLocation")
                            val endLocation = journeyObject.getString("endLocation")
                            val duration = journeyObject.getInt("duration")
                            val fare = journeyObject.getDouble("fare")
                            val date = journeyObject.getString("date")

                            val journey = Journey(startLocation, endLocation, duration, fare, date)
                            journeyList.add(journey)
                        }

                        // Update the RecyclerView with the retrieved data on the UI thread
                        runOnUiThread {
                            journeyAdapter.notifyDataSetChanged()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                showToast("Failed to retrieve journeys")
            }
        })
    }

    /**
     * Display a toast message on the UI thread.
     *
     * @param message The message to display in the toast.
     */
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

}
