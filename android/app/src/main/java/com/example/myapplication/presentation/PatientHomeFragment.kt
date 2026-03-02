package com.example.myapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDoctorHomeBinding
import com.example.myapplication.databinding.FragmentPatientHomeBinding

class PatientHomeFragment: Fragment() {

    private var _binding: FragmentPatientHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")
    private lateinit var viewModel: PatientHomeViewModel
    private val adapter = PrescriptionsListAdapter(
        onItemClick = { item ->
            val action = PatientHomeFragmentDirections.actionPatientHomeToPrescriptionDetails(item.prescription.id)
            findNavController().navigate(action)
        },
        onMarkTakenClick = { item ->
            viewModel.onMarkTakenClicked(item)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PatientHomeViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) {
            adapter.submitList(it)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBarPrescriptions.visibility = View.VISIBLE
            } else {
                binding.progressBarPrescriptions.visibility = View.GONE
            }
        }
        viewModel.isEmpty.observe(this) { isEmpty ->
            if (isEmpty) {
                Toast.makeText(requireContext(), "No presciptions found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackPrescriptions.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSettingsPrescriptions.setOnClickListener {
            findNavController().navigate(R.id.action_patientHome_to_profile)
        }

        binding.tabActive.setOnClickListener {
            binding.tabActive.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_selected)
            binding.tabCompleted.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_unselected)
            viewModel.getActivePrescriptions()
        }

        binding.tabCompleted.setOnClickListener {
            binding.tabCompleted.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_selected)
            binding.tabActive.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_unselected)
            viewModel.getCompletedPrescriptions()
        }

        binding.rvPrescriptions.adapter = adapter
        binding.rvPrescriptions.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}