package com.example.myapplication.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentCreatePrescriptionBinding
import com.example.myapplication.domain.Medicine

class CreatePrescriptionFragment: Fragment() {

    private var _binding: FragmentCreatePrescriptionBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")
    private lateinit var viewModel: CreatePrescriptionViewModel

    private val args: CreatePrescriptionFragmentArgs by navArgs()

    private val searchRvAdapter = SearchMedicineAdapter(onItemClick = { medicine ->
        viewModel.selectMedicine(medicine)
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CreatePrescriptionViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.medicineSuggestions.observe(this) { suggestions ->
            if (suggestions.isNotEmpty()) {
                showSuggestionsList(suggestions)
            } else {
                hideSuggestionsList()
            }
        }
        viewModel.medicineSuggestions.observe(this) {
            searchRvAdapter.submitList(it)
        }
        viewModel.selectedMedicine.observe(this) { medicine ->
            if (medicine != null) {
                binding.etMedicine.clearFocus()
                binding.etMedicine.setText(medicine.name)
                binding.etDosage.setText(medicine.defaultDosage)
            }
        }
        viewModel.frequency.observe(this) {
            binding.tvTimesPerDay.text = it.toString()
        }
        viewModel.duration.observe(this) {
            binding.tvDuration.text = it.toString()
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.btnPrescribe.isEnabled = false
                binding.progressBarCreatePatient.visibility = View.VISIBLE
                binding.btnPrescribe.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
            } else {
                binding.btnPrescribe.isEnabled = true
                binding.progressBarCreatePatient.visibility = View.GONE
                binding.btnPrescribe.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary_teal)
            }
        }
        viewModel.isSuccess.observe(this) {
            Toast.makeText(requireContext(), "Success!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePrescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupUI()

    }

    private fun setupUI() {

        val patientId = args.patientId

        binding.apply {
            rvSearchMedicine.adapter = searchRvAdapter
            rvSearchMedicine.layoutManager = LinearLayoutManager(context)
            etMedicine.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    if (etMedicine.hasFocus()) {
                        viewModel.onSearchQueryChanged(p0.toString())
                    }
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

                }

            })
            btnPlusTimes.setOnClickListener { viewModel.incFrequencyValue() }
            btnMinusTimes.setOnClickListener { viewModel.decFrequencyValue() }
            btnPlusDuration.setOnClickListener { viewModel.incDurationValue() }
            btnMinusDuration.setOnClickListener { viewModel.decDurationValue() }

            btnPrescribe.setOnClickListener {
                viewModel.savePrescription(
                    patientId,
                    dosage = etDosage.text.toString(),
                    frequency = tvTimesPerDay.text.toString().toInt(),
                    durationStr = tvDuration.text.toString(),
                    notes = etInstructions.text.toString()
                )
            }
        }
    }

    private fun setupNavigation() {
        binding.btnBackPrescription.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPrescribe.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showSuggestionsList(suggestions: List<Medicine>) {
        binding.rvSearchMedicine.visibility = View.VISIBLE

    }

    private fun hideSuggestionsList() {
        binding.rvSearchMedicine.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}