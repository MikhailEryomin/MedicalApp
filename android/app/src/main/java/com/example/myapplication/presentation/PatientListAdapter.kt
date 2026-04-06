package com.example.myapplication.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemPatientBinding
import com.example.myapplication.domain.Patient

class PatientListAdapter(private val onItemClick: (item: Patient) -> Unit): ListAdapter<Patient, PatientListAdapter.PatientViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PatientViewHolder {
        val binding = ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PatientViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class PatientViewHolder(private val binding: ItemPatientBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Patient) {
            val name = "${item.firstName} ${item.lastName}"

            binding.tvPatientName.text = name
            var text = ""
            if (item.age % 10 == 1) {
                text = "год"
            } else if (item.age % 10 == 2) {
                text = "года"
            } else {
                text = "лет"
            }
            binding.tvPatientAge.text = "${item.age} $text"
            binding.tvInitials.text = "${item.firstName.first()}${item.lastName.first()}"
            binding.cardPatient.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Patient>() {
        override fun areItemsTheSame(oldItem: Patient, newItem: Patient): Boolean {
            // Сравниваем ID элементов (уникальный идентификатор)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Patient, newItem: Patient): Boolean {
            // Сравниваем содержимое (если данные изменились)
            return oldItem == newItem
        }
    }
}