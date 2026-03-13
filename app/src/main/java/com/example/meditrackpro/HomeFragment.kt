package com.example.meditrackpro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var tvDateTime: TextView
    private val handler = Handler(Looper.getMainLooper())

    private val clockRunnable = object : Runnable {
        override fun run() {
            updateClock()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDateTime = view.findViewById(R.id.tvDateTime)
        handler.post(clockRunnable)

        // Greeting + username from Firebase
        view.findViewById<TextView>(R.id.tvGreeting).text = getGreeting()
        val displayName = FirebaseAuth.getInstance().currentUser?.displayName
        if (!displayName.isNullOrEmpty()) {
            view.findViewById<TextView>(R.id.tvUserName).text = displayName
        }

        // Remind button
        view.findViewById<Button>(R.id.btnRemind).setOnClickListener {
            Toast.makeText(requireContext(), "⏰ Reminder set!", Toast.LENGTH_SHORT).show()
        }

        // Views
        val alertCard = view.findViewById<CardView>(R.id.alertCard)
        val cardEmptyVitals = view.findViewById<CardView>(R.id.cardEmptyVitals)
        val gridVitals = view.findViewById<View>(R.id.gridVitals)
        val cardEmptyDoses = view.findViewById<CardView>(R.id.cardEmptyDoses)
        val layoutDoses = view.findViewById<LinearLayout>(R.id.layoutDosesContainer)
        val tvDoseProgress = view.findViewById<TextView>(R.id.tvDoseProgress)
        val tvAdherence = view.findViewById<TextView>(R.id.tvAdherence)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressDoses)
        val tvAlertMedName = view.findViewById<TextView>(R.id.tvAlertMedName)

        // Vitals always empty for now
        cardEmptyVitals.visibility = View.VISIBLE
        gridVitals.visibility = View.GONE

        // Use SHARED ViewModel from MainActivity
        val viewModel = (requireActivity() as MainActivity).sharedMedicineViewModel

        viewModel.allMedicines.observe(viewLifecycleOwner) { medicines ->
            if (medicines.isNullOrEmpty()) {
                // Empty state
                alertCard.visibility = View.GONE
                cardEmptyDoses.visibility = View.VISIBLE
                layoutDoses.visibility = View.GONE
                tvDoseProgress.text = "0/0 doses"
                tvAdherence.text = "0%"
                progressBar.progress = 0
            } else {
                // Show data
                alertCard.visibility = View.VISIBLE
                cardEmptyDoses.visibility = View.GONE
                layoutDoses.visibility = View.VISIBLE

                // Update progress
                val total = medicines.size
                tvDoseProgress.text = "0/$total doses"
                tvAdherence.text = "0%"
                progressBar.progress = 0

                // Show first medicine in alert
                val first = medicines.first()
                tvAlertMedName.text = "${first.name} ${first.dose} · ${first.times.split(",").first().trim()}"

                // Populate dose cards
                layoutDoses.removeAllViews()
                medicines.forEach { med ->
                    val card = layoutInflater.inflate(
                        R.layout.item_dose_card,
                        layoutDoses,
                        false
                    )
                    card.findViewById<TextView>(R.id.tvDoseMedName).text = "${med.name} ${med.dose}"
                    card.findViewById<TextView>(R.id.tvDoseTime).text = "⏰ ${med.times}"
                    layoutDoses.addView(card)
                }
            }
        }
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good Morning 👋"
            hour < 17 -> "Good Afternoon 👋"
            else -> "Good Evening 👋"
        }
    }

    private fun updateClock() {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val now = Date()
        tvDateTime.text = "${timeFormat.format(now)} · ${dayFormat.format(now)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(clockRunnable)
    }
}