/**
 * File: ProfileActivity.kt
 * Description: Activity responsible for displaying the user details and the token for travelling.
 * Author: IT21358234_Perera K.A.S.N.
 * Version: 1.0
 * Date: October 15, 2023
 */
package com.example.bustraveler


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log


class ProfileActivity : AppCompatActivity() {

    private lateinit var imageViewQRCode: ImageView
    private lateinit var textViewFullName: TextView
    private lateinit var textViewEmailAddress: TextView
    private lateinit var textViewPhoneNumber: TextView
    private lateinit var textViewUserId: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.hide()

        textViewFullName = findViewById(R.id.textViewFullName)
        textViewEmailAddress = findViewById(R.id.textViewEmailAddress)
        textViewPhoneNumber = findViewById(R.id.textViewPhone)
        val btnJourney: Button = findViewById(R.id.buttonJourney)
        imageViewQRCode = findViewById(R.id.imageViewQRCode)
        textViewUserId = findViewById(R.id.textViewUserId)

        // Replace with the actual user ID

        val packageName = applicationContext.packageName
        val preferencesName = "$packageName.user_preferences"
        val sharedPreferences = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId","").toString()

        textViewUserId.text = userId

        // Make a GET request to retrieve user details
        getUserDetails(userId)
        generateQRCode(userId)
        btnJourney.setOnClickListener {
            var intent = Intent(this, JourneyDetailsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Retrieve user details from the server using the provided user ID.
     *
     * @param userId The unique user ID for the user whose details are to be retrieved.
     */
    private fun getUserDetails(userId: String) {
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

                                var firstName = "undefined"
                                var lastName = ""
                                var emailAddress = "undefined"
                                var phoneNumber = "undefined"
                                if(data.has("firstName")
                                    && data.has("lastName")
                                    && data.has("emailAddress")
                                    && data.has("phoneNumber")){
                                    firstName = data.getString("firstName")
                                    lastName = data.getString("lastName")
                                    emailAddress = data.getString("emailAddress")
                                    phoneNumber = data.getString("phoneNumber")
                                }else{
                                    showToast("Empty data found")
                                }


                                // Update TextViews with user details
                                val fullNameText = "$firstName $lastName"
                                val emailText = "$emailAddress"
                                val phoneText = "$phoneNumber"
                                textViewFullName.text = fullNameText
                                textViewEmailAddress.text = emailText
                                textViewPhoneNumber.text = phoneText
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
     * Generate a QR code for the specified user ID by making a request to the server.
     *
     * @param userId The unique user ID for which a QR code is to be generated.
     */
    private fun generateQRCode(userId: String) {
        // Create an OkHttpClient instance to make HTTP requests
        val client = OkHttpClientSingleton.getInstance()

        // Define the URL for generating a QR code from the server
        val url = "${ApiUrlSingleton.instance.generateTokenEndpoint}/$userId"

        // Create an HTTP GET request to generate the QR code
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        // Send the request and handle the response using a callback
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val qrCodeUrl = response.body?.string()
                        if (qrCodeUrl != null) {
                            showToast(qrCodeUrl)

                            // Generate the QR code from the retrieved URL
                            val qrCodeBitmap = generateQRCodeBitmap(qrCodeUrl)

                            // Update the ImageView with the generated QR code
                            runOnUiThread {
                                imageViewQRCode.setImageBitmap(generateQRCodeBitmap(qrCodeUrl))
                            }
                        }
                    } catch (e: IOException) {
                        //exception handling
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                showToast("Failed to retrieve QR code URL")
            }
        })
    }

    /**
     * Generate a Bitmap from a QR code image URL by extracting and decoding the base64 image data.
     *
     * @param qrCodeUrl The URL of the QR code image, including the base64-encoded image data.
     * @return A Bitmap representing the QR code image, or a placeholder image in case of an error.
     */
    private fun generateQRCodeBitmap(qrCodeUrl: String): Bitmap {
        try {

            // Extract the base64 image data from the URL
            val base64Image = qrCodeUrl.substringAfter("data:image/png;base64,")
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
            Log.d("QRCodeGeneration", "Image bytes length: ${imageBytes.size}")

            // Convert the image bytes into a Bitmap
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        } catch (e: Exception) {

            e.printStackTrace()
            Log.e("QRCodeGeneration", "Error generating QR code: ${e.message}")

            // Handle the error as needed or return a placeholder image
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }
    }

    /**
     * Display a toast message with the specified message text.
     *
     * @param message The text message to display in the toast.
     */
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}

