package com.example.meditrackpro

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Apply font scale from preferences
        val prefs = newBase.getSharedPreferences("appearance_prefs", Context.MODE_PRIVATE)
        val sizeIndex = prefs.getInt("text_size", 1)
        val scale = AppThemeHelper.fontScales[sizeIndex] ?: 1.0f

        val config = Configuration(newBase.resources.configuration)
        config.fontScale = scale
        val scaledContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(scaledContext)
    }
}