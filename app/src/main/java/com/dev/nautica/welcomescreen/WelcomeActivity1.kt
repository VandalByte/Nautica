package com.dev.nautica.welcomescreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dev.nautica.R

class WelcomeActivity1 : AppCompatActivity() {
    val TAG = "WelcomeScreen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome1)  // Ensure this layout file exists
    }

    fun onNextButtonClick(view: android.view.View) {
        Log.i(TAG, "Next button clicked ")
        val intent = Intent(this, WelcomeActivity2::class.java)
        startActivity(intent)
    }
}
