package com.example.meditrackpro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CaregiverSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caregiver_settings)

        val prefs = getSharedPreferences("caregiver_prefs", MODE_PRIVATE)

        val etName     = findViewById<EditText>(R.id.etCaregiverName)
        val etRelation = findViewById<EditText>(R.id.etCaregiverRelation)
        val etPhone    = findViewById<EditText>(R.id.etCaregiverPhone)
        val switchMissed    = findViewById<Switch>(R.id.switchMissedDoseAlert)
        val switchSummary   = findViewById<Switch>(R.id.switchDailySummary)
        val switchEmergency = findViewById<Switch>(R.id.switchEmergencyAlert)

        // Load saved values
        etName.setText(prefs.getString("name", ""))
        etRelation.setText(prefs.getString("relation", ""))
        etPhone.setText(prefs.getString("phone", ""))
        switchMissed.isChecked    = prefs.getBoolean("missed_dose", true)
        switchSummary.isChecked   = prefs.getBoolean("daily_summary", false)
        switchEmergency.isChecked = prefs.getBoolean("emergency", true)
        AppThemeHelper.applyAccentToSwitches(this,
            findViewById(R.id.switchMissedDoseAlert),
            findViewById(R.id.switchDailySummary),
            findViewById(R.id.switchEmergencyAlert)
        )
        AppThemeHelper.applyAccent(this, findViewById(R.id.btnSaveCaregiver))

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnSaveCaregiver).setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter caregiver name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit()
                .putString("name", name)
                .putString("relation", etRelation.text.toString().trim())
                .putString("phone", etPhone.text.toString().trim())
                .putBoolean("missed_dose", switchMissed.isChecked)
                .putBoolean("daily_summary", switchSummary.isChecked)
                .putBoolean("emergency", switchEmergency.isChecked)
                .putBoolean("active", true)
                .apply()

            Toast.makeText(this, "✅ Caregiver saved!", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.btnRemoveCaregiver).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Remove Caregiver")
                .setMessage("Are you sure you want to remove ${etName.text.toString().ifEmpty { "this caregiver" }}?")
                .setPositiveButton("Remove") { _, _ ->
                    prefs.edit().clear().apply()
                    Toast.makeText(this, "Caregiver removed", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}