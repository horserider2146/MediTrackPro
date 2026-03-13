package com.example.meditrackpro

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        // Firebase display name
        auth.currentUser?.displayName?.let { name ->
            if (name.isNotEmpty()) view.findViewById<TextView>(R.id.tvProfileName).text = name
        }

        // Sign out
        view.findViewById<Button>(R.id.btnSignOut).setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        // Edit health info
        view.findViewById<TextView>(R.id.tvEditHealthInfo).setOnClickListener {
            startActivity(Intent(requireContext(), EditHealthInfoActivity::class.java))
        }

        // Caregiver card
        view.findViewById<CardView>(R.id.cardCaregiver).setOnClickListener {
            startActivity(Intent(requireContext(), CaregiverSettingsActivity::class.java))
        }

        // Settings rows
        view.findViewById<View>(R.id.settingNotifications).setOnClickListener {
            startActivity(Intent(requireContext(), NotificationSettingsActivity::class.java))
        }
        view.findViewById<View>(R.id.settingPrivacy).setOnClickListener {
            startActivity(Intent(requireContext(), PrivacySettingsActivity::class.java))
        }
        view.findViewById<View>(R.id.settingEmergency).setOnClickListener {
            startActivity(Intent(requireContext(), EmergencyContactsActivity::class.java))
        }
        view.findViewById<View>(R.id.settingAppearance).setOnClickListener {
            startActivity(Intent(requireContext(), AppAppearanceActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val view = view ?: return
        val prefs = requireContext().getSharedPreferences("health_info_prefs", android.content.Context.MODE_PRIVATE)

        // Health info values
        view.findViewById<TextView>(R.id.tvBloodTypeValue).text =
            prefs.getString("blood_type", "").takeIf { !it.isNullOrEmpty() } ?: "--"
        view.findViewById<TextView>(R.id.tvHeightValue).text =
            prefs.getString("height", "").let { if (!it.isNullOrEmpty()) "$it cm" else "--" }
        view.findViewById<TextView>(R.id.tvWeightValue).text =
            prefs.getString("weight", "").let { if (!it.isNullOrEmpty()) "$it kg" else "--" }
        view.findViewById<TextView>(R.id.tvBMIValue).text =
            prefs.getString("bmi", "").takeIf { !it.isNullOrEmpty() } ?: "--"

        // DOB + Gender in profile card
        val dob = prefs.getString("dob", "") ?: ""
        val gender = prefs.getString("gender", "") ?: ""
        val dobGenderText = listOf(dob, gender).filter { it.isNotEmpty() }.joinToString(" · ")
        view.findViewById<TextView>(R.id.tvProfileDOBGender).text =
            if (dobGenderText.isNotEmpty()) "DOB: $dobGenderText" else ""

        // Conditions as dynamic badges
        val conditionsLayout = view.findViewById<LinearLayout>(R.id.layoutConditionBadges)
        conditionsLayout.removeAllViews()
        val conditions = prefs.getString("conditions", "") ?: ""
        if (conditions.isNotEmpty()) {
            val colors = listOf(
                Pair(R.drawable.pill_blue_bg, R.color.blue_accent),
                Pair(R.drawable.pill_warning_bg, R.color.warning),
                Pair(R.drawable.pill_purple_bg, R.color.purple_accent),
                Pair(R.drawable.pill_accent_bg, R.color.accent)
            )
            conditions.split(",").forEachIndexed { index, condition ->
                val trimmed = condition.trim()
                if (trimmed.isNotEmpty()) {
                    val badge = TextView(requireContext()).apply {
                        text = trimmed
                        textSize = 12f
                        setTypeface(null, Typeface.BOLD)
                        val colorPair = colors[index % colors.size]
                        setBackgroundResource(colorPair.first)
                        setTextColor(resources.getColor(colorPair.second, null))
                        setPadding(24, 8, 24, 8)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).also { it.marginEnd = 8 }
                    }
                    conditionsLayout.addView(badge)
                }
            }
        }

        // Caregiver
        val caregiverPrefs = requireContext().getSharedPreferences(
            "caregiver_prefs", android.content.Context.MODE_PRIVATE
        )
        val caregiverName = caregiverPrefs.getString("name", "") ?: ""
        val caregiverRelation = caregiverPrefs.getString("relation", "") ?: ""
        val isActive = caregiverPrefs.getBoolean("active", false)

        if (caregiverName.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tvCaregiverName).text = caregiverName
            view.findViewById<TextView>(R.id.tvCaregiverRelation).text =
                if (caregiverRelation.isNotEmpty()) "$caregiverRelation · Gets missed-dose alerts"
                else "Gets missed-dose alerts"
            view.findViewById<TextView>(R.id.tvCaregiverStatus).text =
                if (isActive) "Active" else "›"
        } else {
            view.findViewById<TextView>(R.id.tvCaregiverName).text = "Not set"
            view.findViewById<TextView>(R.id.tvCaregiverRelation).text = "Tap to add a caregiver"
            view.findViewById<TextView>(R.id.tvCaregiverStatus).text = "›"
        }
    }
}