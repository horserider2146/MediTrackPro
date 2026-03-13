package com.example.meditrackpro

import android.content.Intent
import android.os.Bundle
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

class MedicationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_medications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tab layouts
        val layoutToday = view.findViewById<View>(R.id.layoutToday)
        val layoutAll = view.findViewById<View>(R.id.layoutAll)
        val layoutRefill = view.findViewById<View>(R.id.layoutRefill)
        val btnToday = view.findViewById<Button>(R.id.btnTabToday)
        val btnAll = view.findViewById<Button>(R.id.btnTabAll)
        val btnRefill = view.findViewById<Button>(R.id.btnTabRefill)

        fun showTab(activeBtn: Button, activeLayout: View) {
            listOf(btnToday, btnAll, btnRefill).forEach { btn ->
                btn.backgroundTintList = resources.getColorStateList(R.color.card, null)
                btn.setTextColor(resources.getColor(R.color.text_secondary, null))
            }
            listOf(layoutToday, layoutAll, layoutRefill).forEach { it.visibility = View.GONE }
            activeBtn.backgroundTintList = resources.getColorStateList(R.color.accent, null)
            activeBtn.setTextColor(resources.getColor(R.color.bg_dark, null))
            activeLayout.visibility = View.VISIBLE
        }

        btnToday.setOnClickListener { showTab(btnToday, layoutToday) }
        btnAll.setOnClickListener { showTab(btnAll, layoutAll) }
        btnRefill.setOnClickListener { showTab(btnRefill, layoutRefill) }

        // Add Medicine button
        view.findViewById<Button>(R.id.btnAddMed).setOnClickListener {
            startActivity(Intent(requireContext(), AddMedicineActivity::class.java))
        }

        // Use SHARED ViewModel
        val viewModel = (requireActivity() as MainActivity).sharedMedicineViewModel

        // --- TODAY TAB ---
        val cardEmptyToday = view.findViewById<CardView>(R.id.cardEmptyToday)
        val layoutTodayMeds = view.findViewById<LinearLayout>(R.id.layoutTodayMeds)

        // --- ALL MEDS TAB ---
        val cardEmptyAll = view.findViewById<CardView>(R.id.cardEmptyAll)
        val medListContainer = view.findViewById<LinearLayout>(R.id.medListContainer)

        // --- REFILL TAB ---
        val cardEmptyRefill = view.findViewById<CardView>(R.id.cardEmptyRefill)
        val layoutRefillMeds = view.findViewById<LinearLayout>(R.id.layoutRefillMeds)

        viewModel.allMedicines.observe(viewLifecycleOwner) { medicines ->

            if (medicines.isNullOrEmpty()) {
                // TODAY — empty
                cardEmptyToday.visibility = View.VISIBLE
                layoutTodayMeds.visibility = View.GONE

                // ALL — empty
                cardEmptyAll.visibility = View.VISIBLE
                medListContainer.visibility = View.GONE

                // REFILL — empty
                cardEmptyRefill.visibility = View.VISIBLE
                layoutRefillMeds.visibility = View.GONE

            } else {
                // ===== TODAY TAB =====
                cardEmptyToday.visibility = View.GONE
                layoutTodayMeds.visibility = View.VISIBLE
                layoutTodayMeds.removeAllViews()

                medicines.forEach { med ->
                    val card = layoutInflater.inflate(R.layout.item_medicine, layoutTodayMeds, false)
                    card.findViewById<TextView>(R.id.tvMedName).text = "${med.name} ${med.dose}"
                    card.findViewById<TextView>(R.id.tvMedDetails).text = "${med.times} · Refill in ${med.refillDays} days"
                    card.findViewById<TextView>(R.id.tvDeleteMed).setOnClickListener {
                        viewModel.delete(med)
                        Toast.makeText(requireContext(), "🗑️ ${med.name} removed", Toast.LENGTH_SHORT).show()
                    }
                    layoutTodayMeds.addView(card)
                }

                // ===== ALL MEDS TAB =====
                cardEmptyAll.visibility = View.GONE
                medListContainer.visibility = View.VISIBLE
                medListContainer.removeAllViews()

                medicines.forEach { med ->
                    val card = layoutInflater.inflate(R.layout.item_medicine, medListContainer, false)
                    card.findViewById<TextView>(R.id.tvMedName).text = "${med.name} ${med.dose}"
                    card.findViewById<TextView>(R.id.tvMedDetails).text = "${med.times} · Refill in ${med.refillDays} days"
                    card.findViewById<TextView>(R.id.tvDeleteMed).setOnClickListener {
                        viewModel.delete(med)
                        Toast.makeText(requireContext(), "🗑️ ${med.name} removed", Toast.LENGTH_SHORT).show()
                    }
                    medListContainer.addView(card)
                }

                // ===== REFILL TAB =====
                cardEmptyRefill.visibility = View.GONE
                layoutRefillMeds.visibility = View.VISIBLE
                layoutRefillMeds.removeAllViews()

                medicines.forEach { med ->
                    val refillCard = layoutInflater.inflate(R.layout.item_refill_card, layoutRefillMeds, false)
                    refillCard.findViewById<TextView>(R.id.tvRefillMedName).text = med.name
                    refillCard.findViewById<TextView>(R.id.tvRefillDaysLeft).text = "${med.refillDays} days left"
                    val progress = ((med.refillDays.toFloat() / 30f) * 100).toInt().coerceIn(0, 100)
                    refillCard.findViewById<ProgressBar>(R.id.progressRefill).progress = progress
                    layoutRefillMeds.addView(refillCard)
                }
            }
        }
    }
}