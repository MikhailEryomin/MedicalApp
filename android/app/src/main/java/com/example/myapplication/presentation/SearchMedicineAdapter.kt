package com.example.myapplication.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemSearchPrescriptionBinding
import com.example.myapplication.domain.Medicine
import com.example.myapplication.domain.Prescription

class SearchMedicineAdapter(private val onItemClick: (medicine: Medicine) -> Unit): ListAdapter<Medicine, SearchMedicineAdapter.MedicineViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicineViewHolder {
        val binding = ItemSearchPrescriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MedicineViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class MedicineViewHolder(private val binding: ItemSearchPrescriptionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Medicine) {
            binding.tvMedicineName.text = "${item.name} ${item.defaultDosage}"
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Medicine>() {
        override fun areItemsTheSame(oldItem:  Medicine, newItem: Medicine): Boolean {
            // Сравниваем ID элементов (уникальный идентификатор)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
            // Сравниваем содержимое (если данные изменились)
            return oldItem == newItem
        }
    }

}