package com.example.myapplication.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemPatientGlobalBinding
import com.example.myapplication.domain.Patient

class GlobalPatientAdapter(private val onAttachClick: (item: Patient) -> Unit) :
    ListAdapter<Patient, GlobalPatientAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPatientGlobalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPatientGlobalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Patient) {
            binding.tvPatientName.text = "${item.firstName} ${item.lastName}"
            binding.tvPatientAge.text = "${item.age} years"
            binding.tvInitials.text = "${item.firstName.first()}${item.lastName.first()}"

            binding.btnAttach.setOnClickListener {
                onAttachClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Patient>() {
        override fun areItemsTheSame(oldItem: Patient, newItem: Patient) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Patient, newItem: Patient) = oldItem == newItem
    }
}