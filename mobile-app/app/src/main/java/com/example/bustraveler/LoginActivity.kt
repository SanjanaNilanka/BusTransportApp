/**
 * File: LoginActivity.kt
 * Description: Activity responsible for user login.
 * Author: IT21358234_Perera K.A.S.N.
 * Version: 1.0
 * Date: October 15, 2023
 */
package com.example.bustraveler

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val loginButton: Button = findViewById(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Create an OkHttpClient instance for making HTTP requests
            val client = OkHttpClientSingleton.getInstance()
            val url = ApiUrlSingleton.instance.userLoginEndpoint

            // Create a JSON object with login data
            val json = JSONObject()
            json.put("emailAddress", email)
            json.put("password", password)

            // Create a request body with the JSON data
            val body =
                json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            // Send the HTTP request and handle the response using a callback
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            showToast("Request Sent")

                            val responseData = response.body?.string()
                            val jsonResponse = JSONObject(responseData)
                            val success = jsonResponse.getBoolean("success")
                            val message = jsonResponse.getString("message")
                            val userId = jsonResponse.getString("userId")

                            // Get shared preferences to store user data
                            val packageName = applicationContext.packageName
                            val preferencesName = "$packageName.user_preferences"
                            val sharedPreferences = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

                            // Edit shared preferences to store user data
                            val  editor = sharedPreferences.edit()
                            editor.putString("userEmail",email)
                            editor.putString("userId", userId)
                            editor.apply()

                            runOnUiThread {
                                if (success) {
                                    // Notify the user that login was successful
                                    showToast("Login successful $userId")
                                    // Navigate to the profile when login was success
                                    val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    // Login failed, display an error message
                                    showToast("Login failed: $message")
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    showToast("Login failed")
                }
            })
        }
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
