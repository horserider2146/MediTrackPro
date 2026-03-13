package com.example.meditrackpro

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate

object AppThemeHelper {

    val accentColors = mapOf(
        "green"  to "#00D4AA",
        "blue"   to "#4A9FFF",
        "purple" to "#A78BFA",
        "orange" to "#FFB347",
        "red"    to "#FF6B6B"
    )

    val fontScales = mapOf(
        0 to 0.85f,
        1 to 1.0f,
        2 to 1.15f
    )

    fun applyTheme(context: Context) {
        val prefs = context.getSharedPreferences("appearance_prefs", Context.MODE_PRIVATE)
        val isDark = prefs.getString("theme", "dark") == "dark"
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun getAccentHex(context: Context): String {
        val prefs = context.getSharedPreferences("appearance_prefs", Context.MODE_PRIVATE)
        val key = prefs.getString("accent_color", "green") ?: "green"
        return accentColors[key] ?: "#00D4AA"
    }

    fun getAccentColor(context: Context): Int {
        return Color.parseColor(getAccentHex(context))
    }

    fun getFontScale(context: Context): Float {
        val prefs = context.getSharedPreferences("appearance_prefs", Context.MODE_PRIVATE)
        val sizeIndex = prefs.getInt("text_size", 1)
        return fontScales[sizeIndex] ?: 1.0f
    }

    // ── Call this in every Fragment/Activity's onViewCreated / onCreate ──
    fun applyAccent(context: Context, vararg buttons: Button?) {
        val color = getAccentColor(context)
        val colorStateList = ColorStateList.valueOf(color)
        buttons.filterNotNull().forEach { btn ->
            btn.backgroundTintList = colorStateList
        }
    }

    fun applyAccentToProgressBars(context: Context, vararg bars: ProgressBar?) {
        val color = getAccentColor(context)
        val colorStateList = ColorStateList.valueOf(color)
        bars.filterNotNull().forEach { bar ->
            bar.progressTintList = colorStateList
            // SeekBar-specific tinting handled separately
            if (bar is android.widget.SeekBar) {
                bar.thumbTintList = colorStateList
            }
        }
    }

    fun applyAccentToSwitches(context: Context, vararg switches: Switch?) {
        val color = getAccentColor(context)
        val colorStateList = ColorStateList.valueOf(color)
        switches.filterNotNull().forEach { sw ->
            sw.thumbTintList = colorStateList
            sw.trackTintList = ColorStateList.valueOf(
                Color.argb(80, Color.red(color), Color.green(color), Color.blue(color))
            )
        }
    }

    fun applyAccentToBottomNav(
        context: Context,
        bottomNav: com.google.android.material.bottomnavigation.BottomNavigationView
    ) {
        val color = getAccentColor(context)
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        val colors = intArrayOf(color, Color.parseColor("#8B9DC3"))
        val colorStateList = ColorStateList(states, colors)
        bottomNav.itemIconTintList = colorStateList
        bottomNav.itemTextColor = colorStateList
    }
}