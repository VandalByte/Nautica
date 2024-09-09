package com.dev.nautica

import androidx.core.content.ContextCompat
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var googleSignInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Set the status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        // Initialize the buttons
        googleSignInButton = findViewById(R.id.googleSignInBtn)
        loginButton = findViewById(R.id.loginBtn)

        // Set click listeners for the buttons
        googleSignInButton.setOnClickListener {
            navigateToMainActivity()
        }

        loginButton.setOnClickListener {
            navigateToMainActivity()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun navigateToMainActivity() {
        // Show a toast message
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
        // Navigate to MainActivity
        val intent = Intent(this, HeatmapActivity::class.java)
        startActivity(intent)
        finish() // Finish LoginActivity to prevent user from returning here
    }
}
