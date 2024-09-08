package com.dev.nautica.welcomescreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dev.nautica.LoginActivity
import com.dev.nautica.R

class WelcomeActivity3 : AppCompatActivity() {
    val TAG = "WelcomeScreen3"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome3)

    }
    fun onNextButtonClick(view: android.view.View) {
        Log.i(TAG, "Get Started button clicked ")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}
