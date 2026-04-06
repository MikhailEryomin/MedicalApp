package com.example.myapplication.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemPatientPrescriptionBinding
import com.example.myapplication.databinding.ItemPrescriptionBinding
import com.example.myapplication.domain.Prescription
import com.example.myapplication.domain.PrescriptionStatus

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

            val context = binding.root.context // Получаем context из view
            val unitResId = item.medicine.form.unitNameRes // Получаем ID ресурса (R.plurals.pills)
            val frequency = item.frequency

            binding.tvMedicineName.text = "${item.medicine.name} ${item.medicine.defaultDosage}"
            val status = item.status.displayName
            binding.tvStatus.text = status
            if (status == PrescriptionStatus.ACTIVE.displayName) {
                binding.tvStatus.background = ContextCompat.getDrawable(binding.tvStatus.context, R.drawable.bg_status_active)
            } else if (status == PrescriptionStatus.COMPLETED.displayName) {
                binding.tvStatus.background = ContextCompat.getDrawable(binding.tvStatus.context, R.drawable.bg_status_completed)
            }

            val frequencyText = context.resources.getQuantityString(unitResId, frequency, frequency)
            binding.tvDosage.text = "$frequencyText в день"



            binding.tvStartDate.text = "Начало: ${item.startDate}"
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