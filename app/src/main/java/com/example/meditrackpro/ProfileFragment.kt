package com.example.meditrackpro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

        // Show current user name from Firebase
        auth.currentUser?.displayName?.let { name ->
            if (name.isNotEmpty()) {
                view.findViewById<TextView>(R.id.tvProfileName).text = name
            }
        }

        // Sign out
        view.findViewById<Button>(R.id.btnSignOut).setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        // Caregiver card
        view.findViewById<CardView>(R.id.cardCaregiver).setOnClickListener {
            Toast.makeText(requireContext(), "📲 Caregiver settings — Coming Soon!", Toast.LENGTH_SHORT).show()
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
}