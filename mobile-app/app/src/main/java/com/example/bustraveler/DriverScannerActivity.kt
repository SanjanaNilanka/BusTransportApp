/**
 * File: DriverScannerActivity.kt
 * Description: This is the scanner for scanning the passenger's tokens when them get in the bus and get out the bus.
 * Author: IT21358234_Perera K.A.S.N.
 * Version: 1.0
 * Date: October 15, 2023
 */
package com.example.bustraveler

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class DriverScannerActivity : AppCompatActivity() {

    private lateinit var btnOpenScanner:Button
    private lateinit var tvDisplayUserId:TextView

    private lateinit var textViewName: TextView

    private var finalDistance: Long = 0L

    private lateinit var textViewDistance: TextView
    private lateinit var textViewFare: TextView

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0



    private var fare: Double = 0.0
    private var credit: Double = 0.0

    //overriding the onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_scanner)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        btnOpenScanner = findViewById(R.id.btnOpenScanner)
        tvDisplayUserId = findViewById(R.id.tvDisplayUserId)

        textViewName = findViewById(R.id.tvName)

        textViewDistance= findViewById(R.id.tvDistance)
        textViewFare = findViewById(R.id.tvFare)

        btnOpenScanner.setOnClickListener {
            // Create an instance of IntentIntegrator to initiate barcode scanning
            val intentIntegrator = IntentIntegrator(this@DriverScannerActivity)
            // Set a prompt message to be displayed to the user while scanning
            intentIntegrator.setPrompt("Scanning a Passenger Token")
            // Lock the orientation of the scanning activity
            intentIntegrator.setOrientationLocked(true)
            // Enable a beep sound when a barcode is detected
            intentIntegrator.setBeepEnabled(true)
            // Enable capturing and storing an image of the scanned barcode
            intentIntegrator.setBarcodeImageEnabled(true)
            // Set the desired barcode format to QR_CODE
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            // Initiate the barcode scanning process
            intentIntegrator.initiateScan()
        }
        getCurrentLocation()
    }

    /**
     * Override of the `onActivityResult` method to handle the result of barcode scanning.
     *
     * @param requestCode The request code for the scanning activity.
     * @param resultCode The result code indicating the outcome of the scanning operation.
     * @param data The intent containing the scanning result data.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Parse the result of the barcode scanning operation using the IntentIntegrator utility function.
        // This function extracts the scanned content and other relevant information.
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(intentResult != null){
            val contents = intentResult.contents
            if(contents != null){
                tvDisplayUserId.text = intentResult.contents

                validateUser(intentResult.contents)
            }
        }else{
                tvDisplayUserId.text = "Scan Failed"
        }
    }

    /**
     * Function for validating a user based on their user ID.
     *
     * @param userId The ID of the user to validate.
     */
    private fun validateUser (userId: String){
        // Create an OkHttpClient instance to make HTTP requests
        val client = OkHttpClientSingleton.getInstance()

        // Define the URL for retrieving user data from the backend
        val url = "${ApiUrlSingleton.instance.getUserEndpoint}/$userId"

        // Create an HTTP GET request
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        // Execute the HTTP request and handle the response using a callback
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        // Extract and process the response data
                        val responseData = response.body?.string()
                        val jsonResponse = JSONObject(responseData)
                        val success = jsonResponse.getBoolean("success")

                        runOnUiThread {
                            if (success) {

                                // Extract user details from the response
                                val data = jsonResponse.getJSONObject("data")
                                val firstName = data.getString("firstName")
                                val lastName = data.getString("lastName")
                                val currentCredit = data.getDouble("credit")
                                val getInLocation = data.getString("getInLoc")

                                // Update TextViews with user details
                                val nameText = "$firstName $lastName"
                                textViewName.text = nameText

                                credit = currentCredit
                                val currentLocation = "$latitude, $longitude"
                                showToast("current $currentLocation")
                                // Check for getInLocation and take appropriate actions
                                if (getInLocation == "" || getInLocation == null){
                                    updateGetIn(userId, currentLocation)
                                }else if(getInLocation != "" || getInLocation != null){
                                    closeJourney(userId, getInLocation, currentLocation)
                                }

                            } else {
                                showToast("Failed to retrieve user details")
                            }
                        }
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
     * Function to update the "getInLoc" field in the user's profile with the current location.
     *
     * @param userId The ID of the user to update.
     * @param currentLocation The current location to be updated in the user's profile.
     */
    private fun updateGetIn(userId: String, currentLocation: String){
        // Create an OkHttpClient instance to make HTTP requests
        val client = OkHttpClientSingleton.getInstance()

        // Define the URL for updating the "getInLoc" field in the user's profile
        val url = "${ApiUrlSingleton.instance.updateGetInLocEndpoint}/$userId"

        // Create a JSON object with the new "getInLoc" value
        val json = JSONObject()
        json.put("getInLoc", currentLocation)

        // Create an HTTP request body with the JSON data
        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // Create an HTTP PUT request to update the user's profile
        val request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        // Execute the HTTP request and handle the response using a callback
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseData = response.body?.string()
                        val jsonResponse = JSONObject(responseData)
                        val success = jsonResponse.getBoolean("success")
                        val message = jsonResponse.getString("message")

                        runOnUiThread {
                            if (success) {
                                // Update of "getInLoc" field was successful, display a success message
                                Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
                            } else {
                                // Update failed, display an error message
                                Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    /**
     * Function to close a journey, calculate distance, update user details, and initiate fare calculation.
     *
     * @param userId The ID of the user closing the journey.
     * @param getInLocation The location where the journey started.
     * @param currentLocation The current location where the journey is ending.
     */
    private fun closeJourney(userId: String, getInLocation: String, currentLocation: String){
        showToast("closing journey")
        // Create an OkHttpClient instance to make HTTP requests
        val client = OkHttpClientSingleton.getInstance()

        // Define the URL for updating the "getInLoc" field in the user's profile
        val url = ApiUrlSingleton.instance.getDistanceEndpoint

        // Create a JSON object with the new "getInLoc" value
        val json = JSONObject()
        json.put("getInLoc", getInLocation)
        json.put("getOutLoc", currentLocation)

        // Create an HTTP request body with the JSON data
        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // Create an HTTP POST request to calculate distance
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        // Execute the HTTP request and handle the response using a callback
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseData = response.body?.string()
                        val jsonResponse = JSONObject(responseData)
                        val success = jsonResponse.getBoolean("success")
                        val distance = jsonResponse.getLong("distance")

                        runOnUiThread {
                            if (success) {
                                // Distance calculation was successful, display a success message
                                textViewDistance.text = "$distance"
                                finalDistance = distance
                                // Calculate fare based on the distance
                                fare = calcFare(distance)
                                textViewFare.text = "Rs. $fare"
                                credit -= fare
                                updateGetIn(userId, "")
                                // Update the user's journey details
                                updateJourney(userId, getInLocation, currentLocation)
                                showToast("Journey closed")
                                Toast.makeText(applicationContext,"success to calculate the distance", Toast.LENGTH_SHORT).show()
                            } else {
                                // Distance calculation failed, display an error message
                                Toast.makeText(applicationContext,"fail to calculate the distance", Toast.LENGTH_SHORT).show()
                            }
                        }







                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })

        // Update the "getInLoc" value for the user to an empty string

    }

    /**
     * Function to retrieve the current location using the device's GPS or network location services.
     */
    private fun getCurrentLocation(){
        //check location permission
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        // Get the latitude and longitude of the current location
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {location ->
            if(location != null){
                // Extract and store the latitude and longitude values
                val latitudeValue = location.latitude
                val longitudeValue = location.longitude
                latitude = latitudeValue
                longitude = longitudeValue
            }
        }
    }


    /**
     * Function to update the user's journey details with start and end locations, duration, and fare.
     *
     * @param userId The ID of the user whose journey is being updated.
     * @param startLocation The location where the journey started.
     * @param endLocation The location where the journey ended.
     */
    private fun updateJourney(userId: String, startLocation: String, endLocation:String) {
        // Create an OkHttpClient instance to make HTTP requests
        val client = OkHttpClientSingleton.getInstance()

        // Define the URL for updating the user's journey details
        val url = "${ApiUrlSingleton.instance.updateJourneyEndpoint}/$userId"

        // Create a JSON object with the journey details
        val json = JSONObject()
        json.put("startLocation", startLocation)
        json.put("endLocation", endLocation)
        json.put("duration", finalDistance)
        json.put("fare", fare)

        // Create an HTTP request body with the JSON data
        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // Create an HTTP PUT request to update the user's journey details
        val request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseData = response.body?.string()
                        val jsonResponse = JSONObject(responseData)
                        val success = jsonResponse.getBoolean("success")
                        val message = jsonResponse.getString("message")

                        runOnUiThread {
                            if (success) {
                                // Journey update was successful, display a success message
                                Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
                            } else {
                                // Journey update failed, display an error message
                                Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    /**
     * Calculate the fare based on the given distance.
     *
     * @param distance The distance traveled.
     * @return The calculated fare.
     */
    private fun calcFare(distance: Long): Double {
        // Define the initial fare for the first kilometer and the fare rate per kilometer.
        val firstKmFare = 10.0
        val fareRate = 4.0

        val fare = if (distance > 1) {
            firstKmFare + ((distance - 1) * fareRate)
        } else {
            firstKmFare
        }

        return fare
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