package com.dev.nautica.welcomescreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dev.nautica.R


class WelcomeActivity2 : AppCompatActivity() {
    val TAG = "WelcomeScreen2"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome2)


    }
    fun onNextButtonClick(view: android.view.View) {
        Log.i(TAG, "Next button clicked ")
        val intent = Intent(this, WelcomeActivity3::class.java)
        startActivity(intent)
    }
}
