package com.dev.nautica

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException


class LoginActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 1001 // request code
        private const val PREFS_NAME = "user_data"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_PROFILE_PHOTO_URL = "profile_photo_url"
    }

    private lateinit var googleBtn: Button
    private lateinit var gsc: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        googleBtn = findViewById(R.id.googleBtn)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        googleBtn.setOnClickListener {
            signInWithGoogle()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)

        // Check if user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            // User is already signed in, navigate to MainActivity
            // Getting user info
            val userName = account?.displayName

            Toast.makeText(this, "Welcome back to Woof, $userName", Toast.LENGTH_SHORT).show()
            saveUserData(account)
            navigateToMainActivity()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Finish LoginActivity to prevent user from returning here
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign-In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                // Getting user info
                val userName = account?.displayName

                Toast.makeText(this, "Welcome to Nautica, $userName", Toast.LENGTH_SHORT).show()
                saveUserData(account)
                // Navigate to MainActivity
                navigateToMainActivity()

            } catch (e: ApiException) {
                // Google Sign-In failed
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    /*private fun saveUserData(name: String?, email: String?) {
        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_name", name)
            putString("user_email", email)
            apply()
        }
    }*/
    private fun saveUserData(account: GoogleSignInAccount) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_NAME, account.displayName)
            putString(KEY_USER_EMAIL, account.email)
            putString(KEY_PROFILE_PHOTO_URL, account.photoUrl?.toString())
            apply()
        }
    }
}
