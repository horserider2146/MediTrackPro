package com.example.meditrackpro

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import java.text.SimpleDateFormat
import java.util.*

class AddHealthRecordActivity : BaseActivity() {

    private val viewModel: HealthViewModel by viewModels()
    private var selectedType = "Lab Result"

    override fun onCreate(savedInstanceState: Bundle?) {
        AppThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_health_record)

        val accent        = AppThemeHelper.getAccentColor(this)
        val accentList    = ColorStateList.valueOf(accent)
        val surfaceList   = ColorStateList.valueOf(resources.getColor(R.color.surface, null))
        val bgDark        = resources.getColor(R.color.bg_dark, null)
        val textSecondary = resources.getColor(R.color.text_secondary, null)

        val etTitle  = findViewById<EditText>(R.id.etRecordTitle)
        val etDoctor = findViewById<EditText>(R.id.etDoctorName)
        val etNotes  = findViewById<EditText>(R.id.etNotes)
        val etStatus = findViewById<EditText>(R.id.etStatus)
        val btnSave  = findViewById<Button>(R.id.btnSaveRecord)

        // Apply accent to save button
        btnSave.backgroundTintList = accentList
        btnSave.setTextColor(bgDark)

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        // Type selector buttons
        val typeButtons = mapOf(
            R.id.btnTypeLab          to "Lab Result",
            R.id.btnTypePrescription to "Prescription",
            R.id.btnTypeDoctorVisit  to "Doctor Visit",
            R.id.btnTypeVaccination  to "Vaccination"
        )

        fun updateTypeButtons(selectedId: Int) {
            typeButtons.forEach { (id, _) ->
                val btn = findViewById<Button>(id)
                if (id == selectedId) {
                    btn.backgroundTintList = accentList
                    btn.setTextColor(bgDark)
                } else {
                    btn.backgroundTintList = surfaceList
                    btn.setTextColor(textSecondary)
                }
            }
        }

        // Default selection
        updateTypeButtons(R.id.btnTypeLab)

        typeButtons.forEach { (id, type) ->
            findViewById<Button>(id).setOnClickListener {
                selectedType = type
                updateTypeButtons(id)
            }
        }

        btnSave.setOnClickListener {
            val title  = etTitle.text.toString().trim()
            val doctor = etDoctor.text.toString().trim()
            val notes  = etNotes.text.toString().trim()
            val status = etStatus.text.toString().trim().ifEmpty { "Added" }

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val today     = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
            val fullTitle = if (doctor.isNotEmpty()) "$doctor – $title" else title

            viewModel.insert(
                HealthRecord(
                    title  = fullTitle,
                    type   = selectedType,
                    date   = today,
                    status = status,
                    notes  = notes
                )
            )

            Toast.makeText(this, "✅ Record saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}