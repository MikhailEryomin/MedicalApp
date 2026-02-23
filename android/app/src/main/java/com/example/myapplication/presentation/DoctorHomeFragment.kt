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

class DoctorHomeFragment: Fragment() {

    private var _binding: FragmentDoctorHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")
    private lateinit var viewModel: DoctorHomeViewModel

    private val adapter = PatientListAdapter(
        onItemClick = {item ->
            findNavController().navigate(R.id.action_doctorHome_to_patientDetails)
            //TRANSFERING PATIENT DATA
            // ...
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DoctorHomeViewModel::class.java]
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
        _binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
    }

    private fun bindViews() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_doctorHome_to_profile)
        }

        binding.fabAddPatient.setOnClickListener {
            findNavController().navigate(R.id.action_doctorHome_to_createPatient)
        }

        //recycleView
        binding.rvPatients.adapter = adapter
        binding.rvPatients.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}