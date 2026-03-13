package com.example.meditrackpro

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView

class AppAppearanceActivity : BaseActivity() {

    private var selectedColor = "green"
    private var selectedTheme = "dark"

    override fun onCreate(savedInstanceState: Bundle?) {
        AppThemeHelper.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_appearance)

        val prefs = getSharedPreferences("appearance_prefs", MODE_PRIVATE)
        selectedColor = prefs.getString("accent_color", "green") ?: "green"
        selectedTheme = prefs.getString("theme", "dark") ?: "dark"

        val tvSizeValue = findViewById<TextView>(R.id.tvTextSizeValue)
        val seekBar     = findViewById<SeekBar>(R.id.seekTextSize)
        val sizeLabels  = listOf("Small", "Medium", "Large")

        // Load saved seekbar position
        seekBar.progress = prefs.getInt("text_size", 1)
        tvSizeValue.text = sizeLabels[seekBar.progress]

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSizeValue.text = sizeLabels[progress]
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // ── THEME ──
        val cardDark  = findViewById<CardView>(R.id.cardDarkTheme)
        val cardLight = findViewById<CardView>(R.id.cardLightTheme)

        fun updateThemeSelection() {
            val accentColor = AppThemeHelper.getAccentColor(this)
            val unselectedColor = android.graphics.Color.TRANSPARENT

            cardDark.setCardBackgroundColor(
                if (selectedTheme == "dark") accentColor else android.graphics.Color.parseColor("#0A0F1E")
            )
            cardLight.setCardBackgroundColor(
                if (selectedTheme == "light") accentColor else android.graphics.Color.parseColor("#F0F4FF")
            )

            // Update label colors to show selection
            cardDark.findViewById<TextView>(android.R.id.text1)
            cardDark.getChildAt(0)?.let { layout ->
                if (layout is android.widget.LinearLayout) {
                    (layout.getChildAt(1) as? TextView)?.setTextColor(
                        if (selectedTheme == "dark") android.graphics.Color.parseColor("#0A0F1E")
                        else accentColor
                    )
                }
            }
            cardLight.getChildAt(0)?.let { layout ->
                if (layout is android.widget.LinearLayout) {
                    (layout.getChildAt(1) as? TextView)?.setTextColor(
                        if (selectedTheme == "light") android.graphics.Color.WHITE
                        else android.graphics.Color.parseColor("#0A0F1E")
                    )
                }
            }
        }
        updateThemeSelection()

        cardDark.setOnClickListener {
            selectedTheme = "dark"
            updateThemeSelection()
            Toast.makeText(this, "🌙 Dark theme selected", Toast.LENGTH_SHORT).show()
        }
        cardLight.setOnClickListener {
            selectedTheme = "light"
            updateThemeSelection()
            Toast.makeText(this, "☀️ Light theme selected", Toast.LENGTH_SHORT).show()
        }

        // ── ACCENT COLOR ──
        val colorMap = mapOf(
            R.id.colorGreen  to "green",
            R.id.colorBlue   to "blue",
            R.id.colorPurple to "purple",
            R.id.colorOrange to "orange",
            R.id.colorRed    to "red"
        )

        fun updateColorSelection() {
            colorMap.forEach { (btnId, colorName) ->
                val btn = findViewById<Button>(btnId)
                btn.text = if (colorName == selectedColor) "✓" else ""
                btn.setTextColor(Color.WHITE)
            }
        }
        updateColorSelection()

        colorMap.forEach { (btnId, colorName) ->
            findViewById<Button>(btnId).setOnClickListener {
                selectedColor = colorName
                updateColorSelection()
            }
        }

        // ── BACK ──
        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        // ── SAVE ──
        findViewById<Button>(R.id.btnSaveAppearance).setOnClickListener {
            prefs.edit()
                .putInt("text_size", seekBar.progress)
                .putString("accent_color", selectedColor)
                .putString("theme", selectedTheme)
                .apply()

            Toast.makeText(this, "✅ Appearance saved! Restarting…", Toast.LENGTH_SHORT).show()

            // Restart the whole app to apply all changes
            val intent = packageManager.getLaunchIntentForPackage(packageName)!!
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        }
    }
}