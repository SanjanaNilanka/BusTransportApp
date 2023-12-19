package com.example.bustraveler

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RegisterActivityTest{

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

    /**
     * Negative Test Case for validatePaymentData()
     */
    @Test
    fun testEmptyFieldValidation() {
        // Test with all fields empty, which should return false
        val result1 = emptyFieldValidation("", "", "", "", "", "")
        assertFalse(result1)

        // Test with one field (firstName) empty, which should return false
        val result2 = emptyFieldValidation("", "Doe", "johndoe@example.com", "1234567890", "password", "password")
        assertFalse(result2)

        // Test with multiple fields (firstName, email) empty, which should return false
        val result3 = emptyFieldValidation("", "Doe", "", "1234567890", "password", "password")
        assertFalse(result3)

        // Test with all fields filled in, which should return true
        val result4 = emptyFieldValidation("John", "Doe", "johndoe@example.com", "1234567890", "password", "password")
        assertTrue(result4)
    }
}