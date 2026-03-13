package com.example.meditrackpro

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels

class AddMedicineActivity : BaseActivity() {

    private val viewModel: MedicineViewModel by viewModels()
    private var selectedColor = "accent"

    override fun onCreate(savedInstanceState: Bundle?) {
        AppThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)

        val accent    = AppThemeHelper.getAccentColor(this)
        val accentList = ColorStateList.valueOf(accent)
        val bgDark    = resources.getColor(R.color.bg_dark, null)

        val etName   = findViewById<EditText>(R.id.etMedicineName)
        val etDose   = findViewById<EditText>(R.id.etDose)
        val etTimes  = findViewById<EditText>(R.id.etTimes)
        val etRefill = findViewById<EditText>(R.id.etRefillDays)
        val btnSave  = findViewById<Button>(R.id.btnSaveMedicine)

        // Apply accent to save button
        btnSave.backgroundTintList = accentList
        btnSave.setTextColor(bgDark)

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        // Color selection buttons
        val colorButtons = mapOf(
            R.id.btnColorAccent to "accent",
            R.id.btnColorBlue   to "blue",
            R.id.btnColorPurple to "purple",
            R.id.btnColorGold   to "gold"
        )

        // Set initial selection state
        colorButtons.keys.forEach { id ->
            findViewById<Button>(id).alpha = if (id == R.id.btnColorAccent) 1.0f else 0.4f
        }

        colorButtons.forEach { (btnId, colorName) ->
            findViewById<Button>(btnId).setOnClickListener {
                selectedColor = colorName
                colorButtons.keys.forEach { id ->
                    findViewById<Button>(id).alpha = 0.4f
                }
                findViewById<Button>(btnId).alpha = 1.0f
            }
        }

        btnSave.setOnClickListener {
            val name       = etName.text.toString().trim()
            val dose       = etDose.text.toString().trim()
            val times      = etTimes.text.toString().trim()
            val refillText = etRefill.text.toString().trim()

            when {
                name.isEmpty()       -> Toast.makeText(this, "Enter medicine name", Toast.LENGTH_SHORT).show()
                dose.isEmpty()       -> Toast.makeText(this, "Enter dose", Toast.LENGTH_SHORT).show()
                times.isEmpty()      -> Toast.makeText(this, "Enter time(s)", Toast.LENGTH_SHORT).show()
                refillText.isEmpty() -> Toast.makeText(this, "Enter refill days", Toast.LENGTH_SHORT).show()
                else -> {
                    viewModel.insert(
                        Medicine(
                            name      = name,
                            dose      = dose,
                            times     = times,
                            refillDays = refillText.toInt(),
                            color     = selectedColor
                        )
                    )
                    Toast.makeText(this, "✅ $name saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}