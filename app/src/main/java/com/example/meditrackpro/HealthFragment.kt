package com.example.meditrackpro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class HealthFragment : Fragment() {

    private val viewModel: HealthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_health, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardEmpty = view.findViewById<CardView>(R.id.cardEmptyRecords)
        val layoutRecords = view.findViewById<LinearLayout>(R.id.layoutRecordsContainer)
        val tvCountRecords = view.findViewById<TextView>(R.id.tvCountRecords)
        val tvCountVaccines = view.findViewById<TextView>(R.id.tvCountVaccines)
        val tvCountLab = view.findViewById<TextView>(R.id.tvCountLab)
        val tvCountDoctors = view.findViewById<TextView>(R.id.tvCountDoctors)

        // Add record manually
        view.findViewById<Button>(R.id.btnAddRecord).setOnClickListener {
            startActivity(Intent(requireContext(), AddHealthRecordActivity::class.java))
        }

        // Upload report (file picker)
        view.findViewById<Button>(R.id.btnUploadReport).setOnClickListener {
            Toast.makeText(requireContext(), "📎 Upload feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Observe records
        viewModel.allRecords.observe(viewLifecycleOwner) { records ->

            // Update summary counters
            tvCountRecords.text = records.size.toString()
            tvCountVaccines.text = records.count { it.type == "Vaccination" }.toString()
            tvCountLab.text = records.count { it.type == "Lab Result" }.toString()
            tvCountDoctors.text = records.count { it.type == "Doctor Visit" }.toString()

            if (records.isEmpty()) {
                cardEmpty.visibility = View.VISIBLE
                layoutRecords.visibility = View.GONE
            } else {
                cardEmpty.visibility = View.GONE
                layoutRecords.visibility = View.VISIBLE
                layoutRecords.removeAllViews()

                records.forEach { record ->
                    val card = layoutInflater.inflate(
                        R.layout.item_health_record,
                        layoutRecords,
                        false
                    )
                    card.findViewById<TextView>(R.id.tvRecordTitle).text = record.title
                    card.findViewById<TextView>(R.id.tvRecordSubtitle).text =
                        "${record.type} · ${record.date}"
                    card.findViewById<TextView>(R.id.tvRecordStatus).text = record.status

                    // Color status badge by type
                    val (bgDrawable, textColor) = when (record.type) {
                        "Lab Result" -> Pair(R.drawable.pill_accent_bg, R.color.accent)
                        "Prescription" -> Pair(R.drawable.pill_blue_bg, R.color.blue_accent)
                        "Doctor Visit" -> Pair(R.drawable.pill_purple_bg, R.color.purple_accent)
                        "Vaccination" -> Pair(R.drawable.pill_gold_bg, R.color.gold)
                        else -> Pair(R.drawable.pill_accent_bg, R.color.accent)
                    }
                    val statusView = card.findViewById<TextView>(R.id.tvRecordStatus)
                    statusView.setBackgroundResource(bgDrawable)
                    statusView.setTextColor(resources.getColor(textColor, null))

                    // Emoji icon by type
                    card.findViewById<TextView>(R.id.tvRecordIcon).text = when (record.type) {
                        "Lab Result" -> "🧪"
                        "Prescription" -> "📋"
                        "Doctor Visit" -> "🏥"
                        "Vaccination" -> "💉"
                        else -> "📄"
                    }

                    // Long press to delete
                    card.setOnLongClickListener {
                        viewModel.delete(record)
                        Toast.makeText(requireContext(), "🗑️ Record deleted", Toast.LENGTH_SHORT).show()
                        true
                    }

                    card.setOnClickListener {
                        Toast.makeText(
                            requireContext(),
                            "📄 ${record.title}\n${record.notes.ifEmpty { "No notes" }}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    layoutRecords.addView(card)
                }
            }
        }
    }
}