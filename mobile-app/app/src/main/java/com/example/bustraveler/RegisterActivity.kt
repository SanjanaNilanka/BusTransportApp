/**
 * File: RegisterActivity.kt
 * Description: Activity responsible for user sign up.
 * Author: IT21358234_Perera K.A.S.N.
 * Version: 1.0
 * Date: October 15, 2023
 */
package com.example.bustraveler;

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bustraveler.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        val firstNameEditText: EditText = findViewById(R.id.editTextFirstName)
        val lastNameEditText: EditText = findViewById(R.id.editTextLastName)
        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val phoneEditText: EditText = findViewById(R.id.editTextPhone)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val retypePasswordEditText: EditText = findViewById(R.id.editTextRetypePassword)
        val registerButton: Button = findViewById(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val password = passwordEditText.text.toString()
            val retypePassword = retypePasswordEditText.text.toString()

            if (emptyFieldValidation(firstName, lastName, email, phone, password, retypePassword)) {

                if (password == retypePassword) {
                    // Create an OkHttpClient instance for making an HTTP POST request
                    val client = OkHttpClientSingleton.getInstance()
                    val url = ApiUrlSingleton.instance.userRegEndpoint

                    // Create a JSON object with registration data
                    val json = JSONObject()
                    json.put("firstName", firstName)
                    json.put("lastName", lastName)
                    json.put("emailAddress", email)
                    json.put("phoneNumber", phone)
                    json.put("password", password)

                    // Create a request body with the JSON data
                    val body =
                        json.toString()
                            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                    val request = Request.Builder()
                        .url(url)
                        .post(body)
                        .build()

                    // Send the HTTP request and handle the response using a callback
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
                                            // Notify user that registration was successful
                                            Toast.makeText(
                                                applicationContext,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                            startActivity(intent)
                                        } else {
                                            // Registration failed, display an error message
                                            Toast.makeText(
                                                applicationContext,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
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
                }else{
                    Toast.makeText(this, "Password and retype password are not same", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "All fields Should be filled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Validate that all specified fields are not blank (empty).
     *
     * @param firstName First name input.
     * @param lastName Last name input.
     * @param email Email input.
     * @param phone Phone input.
     * @param password Password input.
     * @param retypePassword Retyped password input.
     * @return `true` if all fields are not blank; `false` otherwise.
     */
    private fun emptyFieldValidation (firstName: String,
                                      lastName:String,
                                      email: String,
                                      phone: String,
                                      password: String,
                                      retypePassword:String
    ):Boolean{
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                password.isNotBlank() &&
                retypePassword.isNotBlank()
    }


}
