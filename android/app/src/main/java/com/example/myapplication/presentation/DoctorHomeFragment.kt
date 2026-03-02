package com.example.myapplication.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
        onItemClick = {patient ->
            val action = DoctorHomeFragmentDirections.actionDoctorHomeToPatientDetails(patient.id)
            findNavController().navigate(action)
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
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("refresh_patients")?.observe(this) {
            if (it) {
                viewModel.loadMyPatients()
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refresh_patients")
            }
        }
        bindViews()
    }

    private fun bindViews() {

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_doctorHome_to_profile)
        }

        binding.fabAddPatient.setOnClickListener {
            findNavController().navigate(R.id.action_doctorHome_to_createPatient)
        }

        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                viewModel.searchPatients(p0.toString())
            }

        })

        //recycleView
        binding.rvPatients.adapter = adapter
        binding.rvPatients.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}