package com.example.meditrackpro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var selectedGender = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val etFullName = findViewById<android.widget.EditText>(R.id.etFullName)
        val etEmail = findViewById<android.widget.EditText>(R.id.etEmail)
        val etDOB = findViewById<android.widget.EditText>(R.id.etDOB)
        val etPassword = findViewById<android.widget.EditText>(R.id.etPassword)
        val etConfirm = findViewById<android.widget.EditText>(R.id.etConfirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        val btnMale = findViewById<Button>(R.id.btnMale)
        val btnFemale = findViewById<Button>(R.id.btnFemale)
        val btnOther = findViewById<Button>(R.id.btnOther)

        // Gender selection
        fun selectGender(selected: Button, vararg others: Button, gender: String) {
            selectedGender = gender
            selected.backgroundTintList = getColorStateList(R.color.accent)
            selected.setTextColor(getColor(R.color.bg_dark))
            others.forEach {
                it.backgroundTintList = getColorStateList(R.color.surface)
                it.setTextColor(getColor(R.color.text_secondary))
            }
        }

        btnMale.setOnClickListener { selectGender(btnMale, btnFemale, btnOther, gender = "Male") }
        btnFemale.setOnClickListener { selectGender(btnFemale, btnMale, btnOther, gender = "Female") }
        btnOther.setOnClickListener { selectGender(btnOther, btnMale, btnFemale, gender = "Other") }

        // Back
        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvGoToSignIn).setOnClickListener { finish() }

        // Sign Up
        btnSignUp.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val dob = etDOB.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm = etConfirm.text.toString().trim()

            when {
                name.isEmpty() -> Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
                email.isEmpty() -> Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
                dob.isEmpty() -> Toast.makeText(this, "Enter your date of birth", Toast.LENGTH_SHORT).show()
                selectedGender.isEmpty() -> Toast.makeText(this, "Select your gender", Toast.LENGTH_SHORT).show()
                password.isEmpty() -> Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT).show()
                password.length < 8 -> Toast.makeText(this, "Password must be 8+ characters", Toast.LENGTH_SHORT).show()
                password != confirm -> Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                else -> {
                    btnSignUp.isEnabled = false
                    btnSignUp.text = "Creating Account..."

                    // Create account with Firebase
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Update display name
                                val user = auth.currentUser
                                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest
                                    .Builder()
                                    .setDisplayName(name)
                                    .build()

                                user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener {
                                        Toast.makeText(this, "Welcome $name! 🎉", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                            } else {
                                btnSignUp.isEnabled = true
                                btnSignUp.text = "Create Account"
                                val errorMsg = task.exception?.message ?: "Sign up failed"
                                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
        }
    }
}