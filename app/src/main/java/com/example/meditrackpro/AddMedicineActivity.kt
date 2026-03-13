package com.example.meditrackpro

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class AddMedicineActivity : AppCompatActivity() {

    private val viewModel: MedicineViewModel by viewModels()
    private var selectedColor = "accent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)

        val etName = findViewById<android.widget.EditText>(R.id.etMedicineName)
        val etDose = findViewById<android.widget.EditText>(R.id.etDose)
        val etTimes = findViewById<android.widget.EditText>(R.id.etTimes)
        val etRefill = findViewById<android.widget.EditText>(R.id.etRefillDays)

        // Back button
        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        // Color selection
        val colorButtons = mapOf(
            R.id.btnColorAccent to "accent",
            R.id.btnColorBlue to "blue",
            R.id.btnColorPurple to "purple",
            R.id.btnColorGold to "gold"
        )

        colorButtons.forEach { (btnId, colorName) ->
            findViewById<Button>(btnId).setOnClickListener {
                selectedColor = colorName
                // Show selection with alpha
                colorButtons.keys.forEach { id ->
                    findViewById<Button>(id).alpha = 0.4f
                }
                findViewById<Button>(btnId).alpha = 1.0f
                Toast.makeText(this, "Color: $colorName selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Save button
        findViewById<Button>(R.id.btnSaveMedicine).setOnClickListener {
            val name = etName.text.toString().trim()
            val dose = etDose.text.toString().trim()
            val times = etTimes.text.toString().trim()
            val refillText = etRefill.text.toString().trim()

            when {
                name.isEmpty() -> Toast.makeText(this, "Enter medicine name", Toast.LENGTH_SHORT).show()
                dose.isEmpty() -> Toast.makeText(this, "Enter dose", Toast.LENGTH_SHORT).show()
                times.isEmpty() -> Toast.makeText(this, "Enter time(s)", Toast.LENGTH_SHORT).show()
                refillText.isEmpty() -> Toast.makeText(this, "Enter refill days", Toast.LENGTH_SHORT).show()
                else -> {
                    val medicine = Medicine(
                        name = name,
                        dose = dose,
                        times = times,
                        refillDays = refillText.toInt(),
                        color = selectedColor
                    )
                    viewModel.insert(medicine)
                    Toast.makeText(this, "✅ $name saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}