package com.example.myapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var selectedRole = "DOCTOR" //to viewmodel in future
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding) {
            tabDoctor.setOnClickListener {
                selectedRole = "DOCTOR"
                tabDoctor.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                tabPatient.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
                tabDoctor.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_active)
                tabPatient.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_inactive)
            }
            tabPatient.setOnClickListener {
                selectedRole = "PATIENT"
                tabPatient.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                tabDoctor.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
                tabDoctor.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_inactive)
                tabPatient.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_active)
            }
        }
        binding.btnLogin.setOnClickListener {
            when (selectedRole) {
                "DOCTOR" -> {
                    findNavController().navigate(R.id.action_auth_to_doctorHome)
                }

                "PATIENT" -> {
                    findNavController().navigate(R.id.action_auth_to_patientHome)
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        "The role $selectedRole is not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}