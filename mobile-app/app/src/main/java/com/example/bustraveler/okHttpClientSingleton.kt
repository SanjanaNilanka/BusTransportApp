/**
 * File: okHttpClientSingleton.kt
 * Description: This file defines a singleton object that provides a single instance of the OkHttpClient for making HTTP requests in the Bus Traveler application.
 * Author: IT21358234_Perera K.A.S.N.
 * Version: 1.0
 * Date: October 15, 2023
 */

package com.example.bustraveler

import okhttp3.OkHttpClient

object OkHttpClientSingleton {
    private var instance: OkHttpClient? = null

    // Provide a way to get the Singleton instance
    fun getInstance(): OkHttpClient {
        if (instance == null) {
            instance = OkHttpClient()
        }
        return instance!!
    }
}