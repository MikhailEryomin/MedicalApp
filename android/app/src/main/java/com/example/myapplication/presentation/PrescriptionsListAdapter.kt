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
            binding.tvPrescMedicine.text = item.prescription.medicine.name
            binding.llPrescription.setOnClickListener {
                onItemClick(item)
            }

            binding.progressBar.max = 100
            binding.progressBar.progress = item.progressPercent
            binding.tvPillsCount.text = "${item.takenCount} / ${item.totalCount} taken"
            binding.tvPrescDates.text = "${item.prescription.startDate} - ${item.prescription.endDate}"
            binding.tvPrescDosage.text = "${item.prescription.frequency} pills per day"

            binding.btnMarkTaken.setOnClickListener {
                onMarkTakenClick(item)
            }

            binding.btnMarkTaken.isEnabled = !item.isCompleted
            binding.btnMarkTaken.text = if (item.isCompleted) "Completed" else "Mark as Taken"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PrescriptionUiModel>() {
        override fun areItemsTheSame(oldItem:  PrescriptionUiModel, newItem: PrescriptionUiModel): Boolean {
            // Сравниваем ID элементов (уникальный идентификатор)
            return oldItem.prescription.id == newItem.prescription.id
        }

        override fun areContentsTheSame(oldItem: PrescriptionUiModel, newItem: PrescriptionUiModel): Boolean {
            // Сравниваем содержимое (если данные изменились)
            return oldItem == newItem
        }
    }

    }



