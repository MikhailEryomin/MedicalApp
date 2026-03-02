package com.example.myapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentCreatePatientBinding
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.domain.Gender
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CreatePatientFragment: Fragment() {

    private var _binding: FragmentCreatePatientBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")
    private lateinit var viewModel: CreatePatientViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CreatePatientViewModel::class.java]
        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePatientBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.selectedGender.observe(this) { gender ->
            if (gender == Gender.MALE) {
                binding.btnMale.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_gender_active)
                binding.btnFemale.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_gender_inactive)
                binding.btnMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_teal))
                binding.btnFemale.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            } else if (gender == Gender.FEMALE) {
                binding.btnFemale.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_gender_active)
                binding.btnMale.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_gender_inactive)
                binding.btnFemale.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_teal))
                binding.btnMale.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            }
        }
        viewModel.isSuccess.observe(this) {
            if (it) {
                Toast.makeText(requireContext(), "Success!", Toast.LENGTH_SHORT).show()
                findNavController().previousBackStackEntry?.savedStateHandle?.set("refresh_patients", true)
                findNavController().popBackStack()
            }
        }
        viewModel.errorMessage.observe(this) { msg ->
            if (msg != null) {
                Toast.makeText(requireContext(), "Error: ${msg}!", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBarCreatePatient.visibility = View.VISIBLE
                binding.btnCreatePatient.isEnabled = false
                binding.btnCreatePatient.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
            } else {
                binding.progressBarCreatePatient.visibility = View.GONE
                binding.btnCreatePatient.isEnabled = true
                binding.btnCreatePatient.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary_teal)
            }
        }
        viewModel.allergies.observe(this) { list ->
            renderChips(binding.chipGroupAllergies, list) { itemToRemove ->
                viewModel.removeAllergy(itemToRemove)
            }
        }

        viewModel.diseases.observe(this) { list ->
            renderChips(binding.chipGroupDiseases, list) { itemToRemove ->
                viewModel.removeDisease(itemToRemove)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreatePatient.setOnClickListener {
            findNavController().navigate(R.id.action_createPatientFragment_to_doctorHomeFragment)
        }

        binding.btnBackAddPatient.setOnClickListener {
            findNavController().navigateUp()
        }

        setupDynamicLists()


        with(binding) {
            btnMale.setOnClickListener {
                viewModel.setGender(Gender.MALE)
            }
            btnFemale.setOnClickListener {
                viewModel.setGender(Gender.FEMALE)
            }
        }

        binding.btnCreatePatient.setOnClickListener {
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val birthDate = binding.etDob.text.toString()
            val email = binding.etPatientEmail.text.toString()
            viewModel.createPatient(firstName, lastName, birthDate, email)
        }
    }

    private fun setupDynamicLists() {
        binding.btnAddAllergy.setOnClickListener {
            val text = binding.etAllergy.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.addAllergy(text)
                binding.etAllergy.text?.clear()
            }
        }

        binding.btnAddDisease.setOnClickListener {
            val text = binding.etDisease.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.addDisease(text)
                binding.etDisease.text?.clear()
            }
        }
    }

    private fun renderChips(
        chipGroup: ChipGroup,
        items: List<String>,
        onRemove: (String) -> Unit
    ) {
        chipGroup.removeAllViews()

        items.forEach { item ->
            val chip = Chip(requireContext()).apply {
                text = item
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    onRemove(item)
                }
                setChipBackgroundColorResource(R.color.background_gray)
                setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            }
            chipGroup.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}