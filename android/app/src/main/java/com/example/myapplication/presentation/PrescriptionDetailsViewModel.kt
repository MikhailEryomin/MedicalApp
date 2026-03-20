package com.example.myapplication.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.PatientRepository
import com.example.myapplication.domain.Prescription
import com.example.myapplication.domain.PrescriptionUiModel
import kotlinx.coroutines.launch

class PrescriptionDetailsViewModel: ViewModel() {

    private val repository = PatientRepository()

    private var _uiState = MutableLiveData<PrescriptionUiModel>()
    val uiState get() = _uiState

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadPrescription(prescriptionId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val patientProfile = repository.getPatientProfile()

                if (patientProfile == null) {
                    Log.d("TAG", "PatientProfile is not found!")
                    return@launch
                }

                val prescription = repository.getPrescriptionById(patientId = patientProfile.id, prescriptionId)

                if (prescription == null) {
                    Log.d("TAG", "Prescription is not found!")
                    return@launch
                }

                val taken = prescription.takenCount
                val total = prescription.totalDoses
                val percent = if (total > 0) (taken * 100) / total else 0
                val doctor = repository.getDoctorById(prescription.doctorId)

                val doctorName =
                    if (doctor != null) "${doctor.firstName} ${doctor.lastName}" else ""

                val uiModel = PrescriptionUiModel(
                    prescription = prescription,
                    takenCount = taken,
                    totalCount = total,
                    progressPercent = percent,
                    isCompleted = taken >= total,
                    doctorName = doctorName
                )

                _uiState.value = uiModel

            } catch (e: Exception) {
                // Обработка сетевых ошибок
            } finally {
                _isLoading.value = false
            }
        }
    }

}