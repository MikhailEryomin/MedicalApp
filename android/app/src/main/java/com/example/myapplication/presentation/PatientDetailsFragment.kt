package com.example.myapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPatientDetailsBinding

class PatientDetailsFragment: Fragment() {

    private var _binding: FragmentPatientDetailsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabCreatePrescription.setOnClickListener {
            findNavController().navigate(R.id.action_patientDetails_to_createPrescription)
            //TRANSFERING SELECTED PATIENT DATA
            //....
        }

        binding.btnBackProfile.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}