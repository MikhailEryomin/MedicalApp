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
import com.example.myapplication.presentation.PatientListAdapter.DiffCallback

class PrescriptionsListAdapter(private val onItemClick: (item: Prescription) -> Unit): ListAdapter<Prescription, PrescriptionsListAdapter.PrescriptionViewHolder>(DiffCallback()) {

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
        fun bind(item: Prescription) {
            binding.tvPrescMedicine.text = item.name
            binding.tvPillsCount.text = "${item.pillsCount} of ${item.pillsCount} pills left"
            binding.tvPrescDoctor.text = item.doctorName
            binding.llPrescription.setOnClickListener { onItemClick(item) }

            binding.btnMarkTaken.setOnClickListener {  }
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



