package com.dev.nautica

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val requestExactAlarmPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Handle the case where the permission is not granted
            Toast.makeText(this, "Exact alarm permission is required for reminders.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check and request exact alarm permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                requestExactAlarmPermissionLauncher.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
        }

        // Create Notification Channel
        createNotificationChannel()

        // Initialize views and fragments
        val fragmentNameTextView: TextView = findViewById(R.id.fragmentNameTextView)
        val profileImageView: ImageView = findViewById(R.id.profileImageView)

        val homeFragment = HomeFragment()
        val settingsFragment = SettingsFragment()

        // Set initial fragment
        setCurrentFragment(homeFragment)
        fragmentNameTextView.text = getString(R.string.menu_home)

        // Setup Bottom Navigation View
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuHome -> {
                    setCurrentFragment(homeFragment)
                    fragmentNameTextView.text = getString(R.string.menu_home)
                }
                R.id.menuSettings -> {
                    setCurrentFragment(settingsFragment)
                    fragmentNameTextView.text = getString(R.string.menu_settings)
                }
            }
            true
        }

        // Handle click on profileImageView to navigate to SettingsFragment
        profileImageView.setOnClickListener {
            setCurrentFragment(settingsFragment)
            fragmentNameTextView.text = getString(R.string.menu_settings)
            // Optionally, you can handle other UI changes or actions here
        }

        // Set listener for profile photo change in SettingsFragment
        settingsFragment.setOnProfilePhotoChangedListener(object : SettingsFragment.OnProfilePhotoChangedListener {
            override fun onProfilePhotoChanged(photoUrl: String?) {
                Glide.with(this@MainActivity)
                    .load(photoUrl)
                    .placeholder(R.drawable.img)  // Placeholder image resource
                    .error(R.drawable.img)  // Error image resource
                    .circleCrop()  // Crop the image into a circle
                    .into(profileImageView)  // Load image into profileImageView
            }
        })
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.flFragment, fragment)
            .commit()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ReminderChannel"
            val descriptionText = "Channel for Reminder Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("REMINDER_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
