package com.example.meditrackpro

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AppAppearanceActivity : AppCompatActivity() {

    private var selectedColor = "green"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_appearance)

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        val prefs = getSharedPreferences("appearance_prefs", MODE_PRIVATE)
        val tvSizeValue = findViewById<TextView>(R.id.tvTextSizeValue)
        val seekBar = findViewById<SeekBar>(R.id.seekTextSize)

        seekBar.progress = prefs.getInt("text_size", 1)
        selectedColor = prefs.getString("accent_color", "green") ?: "green"

        // Text size labels
        val sizeLabels = listOf("Small", "Medium", "Large")
        tvSizeValue.text = sizeLabels[seekBar.progress]

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSizeValue.text = sizeLabels[progress]
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Theme selection
        findViewById<androidx.cardview.widget.CardView>(R.id.cardDarkTheme).setOnClickListener {
            Toast.makeText(this, "🌙 Dark theme selected", Toast.LENGTH_SHORT).show()
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.cardLightTheme).setOnClickListener {
            Toast.makeText(this, "☀️ Light theme — Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        // Color selection
        mapOf(
            R.id.colorGreen to "green",
            R.id.colorBlue to "blue",
            R.id.colorPurple to "purple",
            R.id.colorOrange to "orange",
            R.id.colorRed to "red"
        ).forEach { (btnId, colorName) ->
            findViewById<Button>(btnId).setOnClickListener {
                selectedColor = colorName
                Toast.makeText(this, "Color: $colorName selected", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnSaveAppearance).setOnClickListener {
            prefs.edit()
                .putInt("text_size", seekBar.progress)
                .putString("accent_color", selectedColor)
                .apply()
            Toast.makeText(this, "✅ Appearance saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}