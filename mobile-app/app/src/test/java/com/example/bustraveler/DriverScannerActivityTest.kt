package com.example.bustraveler

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DriverScannerActivityTest{

    /**
     * Calculate the fare based on the given distance.
     *
     * @param distance The distance traveled.
     * @return The calculated fare.
     */
    private fun calcFare(distance: Double): Double {
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
     * Positive Test Case for calcFare()
     */
    @Test
    fun testCalcFare() {
        // Define the input distance (greater than 1)
        val distance = 2.5

        // Call the calcFare function and store the result
        val calculatedFare = calcFare(distance)

        // Define the expected fare calculation result
        val expectedFare = 10.0 + ((distance - 1) * 4.0)

        // Check if the calculated fare matches the expected result
        assertEquals(expectedFare, calculatedFare, 0.001)
    }


}