package com.dev.nautica

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.dev.nautica.welcomescreen.WelcomeActivity1

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "PREFERENCE"
        private const val FIRST_INSTALL_KEY = "IsFirstInstall"
    }

    private val SPLASH_TIME_OUT: Long = 4000 // 4 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashLogo: ImageView = findViewById(R.id.splash_logo)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        splashLogo.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstTimeLaunch()) {
                markFirstLaunch()
                launchWelcomeScreen()
            } else {
                launchLoginScreen()
            }
        }, SPLASH_TIME_OUT)
    }

    private fun isFirstTimeLaunch(): Boolean {
        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return preferences.getString(FIRST_INSTALL_KEY, "").isNullOrEmpty()
    }

    private fun markFirstLaunch() {
        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(FIRST_INSTALL_KEY, "Yes")
        editor.apply()
    }

    private fun launchWelcomeScreen() {
        val intent = Intent(this, WelcomeActivity1::class.java)
        startActivity(intent)
        finish() // Finish the current activity to prevent back navigation
    }

    private fun launchLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity to prevent back navigation
    }
}
