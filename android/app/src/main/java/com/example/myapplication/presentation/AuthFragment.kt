package com.example.myapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.domain.UserRole

class AuthFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.authProgressBar.isVisible = isLoading
            binding.btnLogin.text = if (isLoading) "" else "Sign In"
            binding.btnLogin.isEnabled = !isLoading
            binding.etEmail.isEnabled = !isLoading
            binding.etPassword.isEnabled = !isLoading
        }

        viewModel.selectedRole.observe(this) {
            bindTabs(it)
        }

        // Логика ошибок
        viewModel.errorMessage.observe(this) { error ->
            binding.tvError.isVisible = error != null
            binding.tvError.text = error
        }

        // Логика навигации (ГЛАВНОЕ)
        viewModel.loginSuccess.observe(this) { role ->
            role?.let {
                if (it == UserRole.DOCTOR) {
                    findNavController().navigate(R.id.action_auth_to_doctorHome)
                } else {
                    findNavController().navigate(R.id.action_auth_to_patientHome)
                }
                viewModel.onNavigationComplete()
            }
        }
    }

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
                viewModel.selectRole(UserRole.DOCTOR)
            }
            tabPatient.setOnClickListener {
                viewModel.selectRole(UserRole.PATIENT)
            }
        }


        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            viewModel.login(email, pass)

        }
    }

    private fun bindTabs(role: UserRole) {
        binding.apply {
            if (role == UserRole.DOCTOR) {
                tabDoctor.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                tabPatient.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
                tabDoctor.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_active)
                tabPatient.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_inactive)
            } else {
                tabPatient.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                tabDoctor.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
                tabDoctor.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_inactive)
                tabPatient.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_active)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}