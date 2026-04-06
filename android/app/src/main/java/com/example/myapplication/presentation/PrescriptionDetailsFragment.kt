package com.example.myapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.databinding.FragmentPrescriptionDetailsBinding
import com.example.myapplication.domain.PrescriptionUiModel

class PrescriptionDetailsFragment: Fragment() {

    private var _binding: FragmentPrescriptionDetailsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")
    private val args: PrescriptionDetailsFragmentArgs by navArgs()
    private lateinit var viewModel: PrescriptionDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PrescriptionDetailsViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) {
            bindViews(it)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBarPrescription.visibility = View.VISIBLE
                binding.sbDetails.visibility = View.GONE
            } else {
                binding.progressBarPrescription.visibility = View.GONE
                binding.sbDetails.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrescriptionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prescriptionId = args.prescriptionId
        viewModel.loadPrescription(prescriptionId)

        binding.btnBackDetails.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bindViews(uiModel: PrescriptionUiModel) {
        binding.apply {

            val unitResId = uiModel.prescription.medicine.form.unitNameRes

            val remaining = uiModel.totalCount - uiModel.takenCount
            tvPillsNumber.text = remaining.toString()

            tvMedicineTitle.text = "${uiModel.prescription.medicine.name} ${uiModel.prescription.dosage}"
            tvCourseDates.text = "${uiModel.prescription.startDate} - ${uiModel.prescription.endDate}"
            tvInstructions.text = uiModel.prescription.notes
            tvDoctor.text = uiModel.doctorName
            circularProgressBar.max = uiModel.totalCount
            circularProgressBar.progress = remaining
            val remainingText = resources.getQuantityString(unitResId, remaining, remaining)
            tvFrequency.text = "$remainingText осталось"
            tvPillsRemaining.text = "$remainingText из ${uiModel.totalCount} осталось"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}