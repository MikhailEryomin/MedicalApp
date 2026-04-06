package com.example.myapplication.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemPatientBinding
import com.example.myapplication.databinding.ItemPatientPrescriptionBinding
import com.example.myapplication.domain.Prescription
import com.example.myapplication.domain.Patient
import com.example.myapplication.domain.PrescriptionStatus
import com.example.myapplication.domain.PrescriptionUiModel
import com.example.myapplication.presentation.PatientListAdapter.DiffCallback

class PrescriptionsListAdapter(private val onItemClick: (item: PrescriptionUiModel) -> Unit,
    private val onMarkTakenClick: (item: PrescriptionUiModel) -> Unit): ListAdapter<PrescriptionUiModel, PrescriptionsListAdapter.PrescriptionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrescriptionViewHolder {
        val binding = ItemPatientPrescriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrescriptionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PrescriptionViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class PrescriptionViewHolder(private val binding: ItemPatientPrescriptionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PrescriptionUiModel) {

            val context = binding.root.context
            val unitResId = item.prescription.medicine.form.unitNameRes
            val frequency = item.prescription.frequency

            binding.tvPrescMedicine.text = "${item.prescription.medicine.name} ${item.prescription.dosage} мг"
            binding.llPrescription.setOnClickListener {
                onItemClick(item)
            }

            binding.progressBar.max = 100
            binding.progressBar.progress = item.progressPercent
            binding.tvPillsLeft.text = "${item.totalCount - item.takenCount} / ${item.totalCount} осталось"
            binding.tvPrescDates.text = "${item.prescription.startDate} - ${item.prescription.endDate}"
            val frequencyText = context.resources.getQuantityString(unitResId, frequency, frequency)
            binding.tvPrescFrequency.text = "$frequencyText в день"
            binding.tvDoctor.text = item.doctorName

            binding.btnMarkTaken.setOnClickListener {
                if (item.isLoading || item.isCompleted) return@setOnClickListener
                onMarkTakenClick(item)
            }

            when {
                item.isCompleted -> {
                    binding.btnMarkTaken.isEnabled = false
                    binding.btnMarkTaken.text = "Завершено"
                    binding.btnMarkTaken.alpha = 0.5f
                }
                item.isLoading -> {
                    binding.btnMarkTaken.isEnabled = false
                    binding.btnMarkTaken.text = "Сохранение..."
                    binding.btnMarkTaken.alpha = 0.7f
                }
                else -> {
                    binding.btnMarkTaken.isEnabled = true
                    binding.btnMarkTaken.text = "Отметиться"
                    binding.btnMarkTaken.alpha = 1.0f
                }
            }

            binding.btnMarkTaken.isEnabled = !item.isCompleted
            binding.btnMarkTaken.text = if (item.isCompleted)  PrescriptionStatus.COMPLETED.displayName else  "Отметиться"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PrescriptionUiModel>() {
        override fun areItemsTheSame(oldItem:  PrescriptionUiModel, newItem: PrescriptionUiModel): Boolean {
            return oldItem.prescription.id == newItem.prescription.id
        }

        override fun areContentsTheSame(oldItem: PrescriptionUiModel, newItem: PrescriptionUiModel): Boolean {
            return oldItem == newItem
        }
    }

    }



