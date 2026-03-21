package com.example.myapplication.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPatientDetailsBinding
import com.example.myapplication.domain.Gender
import com.example.myapplication.domain.Patient
import com.example.myapplication.domain.Prescription

class PatientDetailsFragment : Fragment() {

    private var _binding: FragmentPatientDetailsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")
    private lateinit var viewModel: PatientDetailsViewModel

    private val args: DoctorHomeFragmentArgs by navArgs()
    private val prescriptionsAdapter = PatientPrescriptionsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PatientDetailsViewModel::class.java]
        observeViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPrescriptions.adapter = prescriptionsAdapter
        binding.rvPrescriptions.layoutManager = LinearLayoutManager(context)

        val patientId = args.patientId
        viewModel.loadPatient(patientId)
        viewModel.loadPrescriptions(patientId)


        binding.fabCreatePrescription.setOnClickListener {
            val action = PatientDetailsFragmentDirections.actionPatientDetailsToCreatePrescription(patientId)
            findNavController().navigate(action)
        }

        binding.btnBackProfile.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.patient.observe(this) {
            bindPatientViews(it)
        }
        viewModel.prescriptions.observe(this) {
            bindPrescriptionsViews(it)
        }
        viewModel.isGeneralLoading.observe(this) { isVisible ->
            if (isVisible) {
                binding.tvHeaderName.text = ""
                binding.progressBarGeneral.visibility = View.VISIBLE
                binding.cvGeneral.visibility = View.GONE
            } else {
                binding.progressBarGeneral.visibility = View.GONE
                binding.cvGeneral.visibility = View.VISIBLE
            }
        }
        viewModel.isPrescriptionsLoading.observe(this) { isVisible ->
            if (isVisible) {
                binding.progressBarPrescriptions.visibility = View.VISIBLE
                binding.rvPrescriptions.visibility = View.GONE
            } else {
                binding.progressBarPrescriptions.visibility = View.GONE
                binding.rvPrescriptions.visibility = View.VISIBLE
            }
        }
    }

    private fun bindPatientViews(patient: Patient) {
        binding.apply {

            tvHeaderName.text = "${patient.firstName} ${patient.lastName}"
            tvFullName.text = tvHeaderName.text
            tvAge.text = "Возраст: ${patient.age} лет"
            tvGender.text = when (patient.gender) {
                Gender.MALE -> "Мужской"
                Gender.FEMALE -> "Женский"
            }
            //tvEmail.text = patient.email

            layoutAllergies.removeAllViews()
            layoutDiseases.removeAllViews()
            if (patient.allergies.isNotEmpty()) {
                for (allergy in patient.allergies) {
                    val tvAllergy = LayoutInflater.from(layoutAllergies.context)
                        .inflate(R.layout.item_patient_property, layoutAllergies, false) as TextView

                    tvAllergy.text = allergy
                    layoutAllergies.addView(tvAllergy)
                }
            } else {
                //tvAllergies.isVisible = false
            }

            // Хронические заболевания
            if (patient.chronicDiseases.isNotEmpty()) {
                for (disease in patient.chronicDiseases) {
                    val tvDisease = LayoutInflater.from(layoutDiseases.context)
                        .inflate(R.layout.item_patient_property, layoutDiseases, false) as TextView

                    tvDisease.text = disease
                    tvDisease.background = ContextCompat.getDrawable(requireContext(),R.drawable.bg_tag_blue)
                    tvDisease.setTextColor(ContextCompat.getColor(requireContext(), R.color.tag_blue_text))
                    layoutDiseases.addView(tvDisease)
                }
            } else {
                //tvDiseases.isVisible = false
            }
        }
    }

    private fun bindPrescriptionsViews(prescriptions: List<Prescription>) {
        if (prescriptions.isEmpty()) {
            binding.tvNoPrescriptions.visibility = View.VISIBLE
            binding.rvPrescriptions.visibility = View.GONE
        }
        else {
            binding.tvNoPrescriptions.visibility = View.GONE
            binding.rvPrescriptions.visibility = View.VISIBLE
        }
        prescriptionsAdapter.submitList(prescriptions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}