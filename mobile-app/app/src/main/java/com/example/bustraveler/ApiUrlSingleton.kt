/**
 * File: ApiSingleton.kt
 * Description: This file defines a singleton class that centralizes the API endpoint URLs for the Bus Traveler application.
 * Author: IT21358234_Perera K.A.S.N.
 * Version: 1.0
 * Date: October 15, 2023
 */

package com.example.bustraveler

class ApiUrlSingleton private constructor() {
    private val BASE_URL = "http://192.168.8.168:8000/"

    companion object {
        val instance by lazy { ApiUrlSingleton() }
    }

    // Endpoints
    val busRoutesEndpoint = "${BASE_URL}busRoutes/getAllRoutes"
    val paymentsEndpoint = "${BASE_URL}guestPayments/saveGuestPayments"
    val getUserEndpoint = "${BASE_URL}user/getUserById"
    val updateGetInLocEndpoint = "${BASE_URL}user/updateGetInPunch"
    val getDistanceEndpoint = "${BASE_URL}token/getDistance"
    val updateJourneyEndpoint = "${BASE_URL}user/updateJourneys"
    val getJourneyEndpoint = "${BASE_URL}user/getJourneys"
    val userLoginEndpoint = "${BASE_URL}user/login"
    val generateTokenEndpoint = "${BASE_URL}token/generateToken"
    val userRegEndpoint = "${BASE_URL}user/register"
}
