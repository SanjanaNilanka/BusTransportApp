package com.example.bustraveler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        var btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener{
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        var btnLogin = findViewById<Button>(R.id.btnLoginNav)

        btnLogin.setOnClickListener{
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        var btnScanner = findViewById<Button>(R.id.btnScanner)

        btnScanner.setOnClickListener{
            var intent = Intent(this, DriverScannerActivity::class.java)
            startActivity(intent)
        }
    }
}