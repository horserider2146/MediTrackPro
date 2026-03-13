package com.example.meditrackpro

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class EmergencyContactsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts)

        val accent = AppThemeHelper.getAccentColor(this)
        val accentList = ColorStateList.valueOf(accent)
        val bgDark = resources.getColor(R.color.bg_dark, null)

        val btnSave = findViewById<Button>(R.id.btnSaveContacts)
        btnSave.backgroundTintList = accentList
        btnSave.setTextColor(bgDark)

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        val prefs = getSharedPreferences("emergency_prefs", MODE_PRIVATE)

        val etContact1Name     = findViewById<EditText>(R.id.etContact1Name)
        val etContact1Relation = findViewById<EditText>(R.id.etContact1Relation)
        val etContact1Phone    = findViewById<EditText>(R.id.etContact1Phone)
        val etContact2Name     = findViewById<EditText>(R.id.etContact2Name)
        val etContact2Relation = findViewById<EditText>(R.id.etContact2Relation)
        val etContact2Phone    = findViewById<EditText>(R.id.etContact2Phone)

        // Load saved contacts
        etContact1Name.setText(prefs.getString("c1_name", ""))
        etContact1Relation.setText(prefs.getString("c1_relation", ""))
        etContact1Phone.setText(prefs.getString("c1_phone", ""))
        etContact2Name.setText(prefs.getString("c2_name", ""))
        etContact2Relation.setText(prefs.getString("c2_relation", ""))
        etContact2Phone.setText(prefs.getString("c2_phone", ""))

        btnSave.setOnClickListener {
            val name1  = etContact1Name.text.toString().trim()
            val phone1 = etContact1Phone.text.toString().trim()

            if (name1.isEmpty() || phone1.isEmpty()) {
                Toast.makeText(this, "Please fill in the primary contact", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit()
                .putString("c1_name",     name1)
                .putString("c1_relation", etContact1Relation.text.toString().trim())
                .putString("c1_phone",    phone1)
                .putString("c2_name",     etContact2Name.text.toString().trim())
                .putString("c2_relation", etContact2Relation.text.toString().trim())
                .putString("c2_phone",    etContact2Phone.text.toString().trim())
                .apply()

            Toast.makeText(this, "✅ Contacts saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}