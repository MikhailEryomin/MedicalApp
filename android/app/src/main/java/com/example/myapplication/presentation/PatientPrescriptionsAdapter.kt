package com.example.myapplication.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemPatientPrescriptionBinding
import com.example.myapplication.databinding.ItemPrescriptionBinding
import com.example.myapplication.domain.Prescription

class PatientPrescriptionsAdapter(): ListAdapter<Prescription, PatientPrescriptionsAdapter.PrescriptionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrescriptionViewHolder {
        val binding = ItemPrescriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrescriptionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PrescriptionViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class PrescriptionViewHolder(private val binding: ItemPrescriptionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Prescription) {
            binding.tvMedicineName.text = "${item.medicine.name} ${item.medicine.defaultDosage}"
            binding.tvStatus.text = item.status.displayName
            binding.tvDosage.text = "${item.frequency} pills per day"
            binding.tvStartDate.text = "Started: ${item.startDate}"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Prescription>() {
        override fun areItemsTheSame(oldItem:  Prescription, newItem: Prescription): Boolean {
            // Сравниваем ID элементов (уникальный идентификатор)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Prescription, newItem: Prescription): Boolean {
            // Сравниваем содержимое (если данные изменились)
            return oldItem == newItem
        }
    }

}