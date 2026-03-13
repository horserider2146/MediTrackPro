package com.example.meditrackpro

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class EditHealthInfoActivity : BaseActivity() {

    private var selectedGender = "Male"

    override fun onCreate(savedInstanceState: Bundle?) {
        AppThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_health_info)

        val accent       = AppThemeHelper.getAccentColor(this)
        val accentList   = ColorStateList.valueOf(accent)
        val surfaceList  = ColorStateList.valueOf(resources.getColor(R.color.surface, null))
        val bgDark       = resources.getColor(R.color.bg_dark, null)
        val textSecondary = resources.getColor(R.color.text_secondary, null)

        val prefs = getSharedPreferences("health_info_prefs", MODE_PRIVATE)

        val etBloodType  = findViewById<EditText>(R.id.etBloodType)
        val etHeight     = findViewById<EditText>(R.id.etHeight)
        val etWeight     = findViewById<EditText>(R.id.etWeight)
        val etDOB        = findViewById<EditText>(R.id.etDOB)
        val etConditions = findViewById<EditText>(R.id.etConditions)
        val btnMale      = findViewById<Button>(R.id.btnGenderMale)
        val btnFemale    = findViewById<Button>(R.id.btnGenderFemale)
        val btnOther     = findViewById<Button>(R.id.btnGenderOther)
        val btnSave      = findViewById<Button>(R.id.btnSaveHealthInfo)

        // Load saved values
        etBloodType.setText(prefs.getString("blood_type", ""))
        etHeight.setText(prefs.getString("height", ""))
        etWeight.setText(prefs.getString("weight", ""))
        etDOB.setText(prefs.getString("dob", ""))
        etConditions.setText(prefs.getString("conditions", ""))
        selectedGender = prefs.getString("gender", "Male") ?: "Male"

        // Apply accent to save button
        btnSave.backgroundTintList = accentList
        btnSave.setTextColor(bgDark)

        // Gender buttons
        fun updateGenderButtons(selected: String) {
            listOf(btnMale to "Male", btnFemale to "Female", btnOther to "Other")
                .forEach { (btn, label) ->
                    if (label == selected) {
                        btn.backgroundTintList = accentList
                        btn.setTextColor(bgDark)
                    } else {
                        btn.backgroundTintList = surfaceList
                        btn.setTextColor(textSecondary)
                    }
                }
        }

        updateGenderButtons(selectedGender)

        btnMale.setOnClickListener   { selectedGender = "Male";   updateGenderButtons("Male") }
        btnFemale.setOnClickListener { selectedGender = "Female"; updateGenderButtons("Female") }
        btnOther.setOnClickListener  { selectedGender = "Other";  updateGenderButtons("Other") }

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val height = etHeight.text.toString().trim()
            val weight = etWeight.text.toString().trim()

            val bmi = if (height.isNotEmpty() && weight.isNotEmpty()) {
                try {
                    val h = height.toFloat() / 100f
                    val w = weight.toFloat()
                    String.format("%.1f", w / (h * h))
                } catch (e: Exception) { "" }
            } else ""

            prefs.edit()
                .putString("blood_type", etBloodType.text.toString().trim())
                .putString("height", height)
                .putString("weight", weight)
                .putString("bmi", bmi)
                .putString("dob", etDOB.text.toString().trim())
                .putString("gender", selectedGender)
                .putString("conditions", etConditions.text.toString().trim())
                .apply()

            Toast.makeText(this, "✅ Health info saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}