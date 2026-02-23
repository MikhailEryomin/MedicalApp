package com.example.myapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentCreatePatientBinding
import com.example.myapplication.databinding.FragmentProfileBinding

class CreatePatientFragment: Fragment() {

    private var _binding: FragmentCreatePatientBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreatePatient.setOnClickListener {
            findNavController().navigate(R.id.action_createPatientFragment_to_doctorHomeFragment)
            //TRANSFERING CREATED PATIENT DATA
            //....
        }

        binding.btnBackAddPatient.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}