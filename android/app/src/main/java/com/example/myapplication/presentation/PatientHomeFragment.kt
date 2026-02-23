package com.example.myapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            findNavController().navigate(R.id.action_patientHome_to_prescriptionDetails)
            //TRANSFERING PRESCRIPTION DATA
            // ...
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PatientHomeViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.patients.observe(this) {
            adapter.submitList(it)
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

        binding.rvPrescriptions.adapter = adapter
        binding.rvPrescriptions.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}