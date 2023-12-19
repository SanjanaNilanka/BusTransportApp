package com.example.bustraveler.dataclasses

data class Journey(
    val startLocation: String,
    val endLocation: String,
    val duration: Int,
    val fare: Double,
    val date: String
)
