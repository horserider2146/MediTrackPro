package com.example.meditrackpro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class PrivacySettingsActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_settings)

        auth = FirebaseAuth.getInstance()

        val prefs = getSharedPreferences("privacy_prefs", MODE_PRIVATE)
        val switchAnalytics = findViewById<Switch>(R.id.switchAnalytics)
        val switchBiometric = findViewById<Switch>(R.id.switchBiometric)
        val switchBackup = findViewById<Switch>(R.id.switchBackup)

        // Load saved prefs
        switchAnalytics.isChecked = prefs.getBoolean("analytics", true)
        switchBiometric.isChecked = prefs.getBoolean("biometric", false)
        switchBackup.isChecked = prefs.getBoolean("backup", true)
        AppThemeHelper.applyAccentToSwitches(this,
            findViewById(R.id.switchAnalytics),
            findViewById(R.id.switchBiometric),
            findViewById(R.id.switchBackup)
        )
        AppThemeHelper.applyAccent(this, findViewById(R.id.btnSavePrivacy))

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        // Biometric toggle
        switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val biometricManager = BiometricManager.from(this)
                when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        Toast.makeText(this, "Biometric lock enabled!", Toast.LENGTH_SHORT).show()
                        prefs.edit().putBoolean("biometric", true).apply()
                    }
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        Toast.makeText(this, " No biometric hardware found", Toast.LENGTH_SHORT).show()
                        switchBiometric.isChecked = false
                    }
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        Toast.makeText(this, " No fingerprints enrolled. Please set up in device settings.", Toast.LENGTH_LONG).show()
                        switchBiometric.isChecked = false
                    }
                    else -> {
                        Toast.makeText(this, "Biometric not available", Toast.LENGTH_SHORT).show()
                        switchBiometric.isChecked = false
                    }
                }
            } else {
                prefs.edit().putBoolean("biometric", false).apply()
                Toast.makeText(this, "Biometric lock disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Clear all health data
        findViewById<LinearLayout>(R.id.btnClearData).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("⚠️ Clear All Health Data")
                .setMessage("This will permanently delete all your medicines from this device. This cannot be undone.")
                .setPositiveButton("Clear All") { _, _ ->
                    // Use the activity's lifecycleScope directly
                    lifecycleScope.launch {
                        MedicineDatabase.getDatabase(applicationContext)
                            .medicineDao()
                            .clearAllMedicines()
                    }
                    // Clear all SharedPreferences
                    listOf("notif_prefs", "privacy_prefs", "emergency_prefs", "appearance_prefs")
                        .forEach { getSharedPreferences(it, MODE_PRIVATE).edit().clear().apply() }
                    Toast.makeText(this, "🗑️ All health data cleared!", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        // Delete account
        findViewById<LinearLayout>(R.id.btnDeleteAccount).setOnClickListener {
            showDeleteAccountDialog()
        }

        // Save button
        findViewById<Button>(R.id.btnSavePrivacy).setOnClickListener {
            prefs.edit()
                .putBoolean("analytics", switchAnalytics.isChecked)
                .putBoolean("biometric", switchBiometric.isChecked)
                .putBoolean("backup", switchBackup.isChecked)
                .apply()
            Toast.makeText(this, "✅ Privacy settings saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDeleteAccountDialog() {
        val user = auth.currentUser ?: return

        // Inflate password input
        val input = EditText(this).apply {
            hint = "Enter your password to confirm"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            setPadding(40, 30, 40, 30)
        }

        AlertDialog.Builder(this)
            .setTitle("⚠️ Delete Account")
            .setMessage("This will permanently delete your account and all data. Enter your password to confirm.")
            .setView(input)
            .setPositiveButton("Delete Account") { _, _ ->
                val password = input.text.toString().trim()
                if (password.isEmpty()) {
                    Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Re-authenticate then delete
                val email = user.email ?: return@setPositiveButton
                val credential = EmailAuthProvider.getCredential(email, password)

                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        user.delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                // Go to login
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, " Wrong password. Try again.", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}