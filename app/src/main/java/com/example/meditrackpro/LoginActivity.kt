package com.example.meditrackpro

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        AppThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // If already logged in
        if (auth.currentUser != null) {
            val prefs = getSharedPreferences("privacy_prefs", MODE_PRIVATE)
            if (prefs.getBoolean("biometric", false)) {
                setContentView(R.layout.activity_login)
                applyAccentColor()
                checkBiometric()
                return
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)
        applyAccentColor()

        val etEmail   = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignIn  = findViewById<Button>(R.id.btnSignIn)

        btnSignIn.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            when {
                email.isEmpty()    -> Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
                password.isEmpty() -> Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show()
                else -> {
                    btnSignIn.isEnabled = false
                    btnSignIn.text = "Signing In..."

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Welcome back! 👋", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                btnSignIn.isEnabled = true
                                btnSignIn.text = "Sign In"
                                Toast.makeText(
                                    this,
                                    task.exception?.message ?: "Sign in failed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }

        // Forgot password
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email first", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "📧 Reset email sent!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Go to Sign Up
        findViewById<TextView>(R.id.tvGoToSignUp).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun applyAccentColor() {
        val accentColor = AppThemeHelper.getAccentColor(this)
        val colorStateList = ColorStateList.valueOf(accentColor)

        // Sign in button
        findViewById<Button>(R.id.btnSignIn)
            ?.backgroundTintList = colorStateList

        // Accent text views
        listOf(R.id.tvForgotPassword, R.id.tvGoToSignUp).forEach { id ->
            findViewById<TextView>(id)?.setTextColor(accentColor)
        }
    }

    private fun checkBiometric() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(this@LoginActivity, "✅ Verified!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        this@LoginActivity,
                        "Use your password to sign in",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@LoginActivity,
                        "Authentication failed. Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("MediTrack Pro")
            .setSubtitle("Verify your identity to continue")
            .setNegativeButtonText("Use Password")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}