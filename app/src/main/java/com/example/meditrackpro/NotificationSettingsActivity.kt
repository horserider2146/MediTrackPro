package com.example.meditrackpro

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NotificationSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        val prefs = getSharedPreferences("notif_prefs", MODE_PRIVATE)

        val switchDose = findViewById<Switch>(R.id.switchDoseReminders)
        val switchMissed = findViewById<Switch>(R.id.switchMissedDose)
        val switchRefill = findViewById<Switch>(R.id.switchRefill)
        val switchCaregiver = findViewById<Switch>(R.id.switchCaregiver)
        val switchSound = findViewById<Switch>(R.id.switchSound)

        // Load saved preferences
        switchDose.isChecked = prefs.getBoolean("dose_reminders", true)
        switchMissed.isChecked = prefs.getBoolean("missed_dose", true)
        switchRefill.isChecked = prefs.getBoolean("refill", true)
        switchCaregiver.isChecked = prefs.getBoolean("caregiver", false)
        switchSound.isChecked = prefs.getBoolean("sound", true)

        findViewById<Button>(R.id.btnSaveNotifications).setOnClickListener {
            prefs.edit()
                .putBoolean("dose_reminders", switchDose.isChecked)
                .putBoolean("missed_dose", switchMissed.isChecked)
                .putBoolean("refill", switchRefill.isChecked)
                .putBoolean("caregiver", switchCaregiver.isChecked)
                .putBoolean("sound", switchSound.isChecked)
                .apply()
            Toast.makeText(this, "✅ Preferences saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}