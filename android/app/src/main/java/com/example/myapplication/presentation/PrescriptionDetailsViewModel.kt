package com.example.myapplication.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.PatientRepository
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

                val prescription = repository.getMyPrescriptionById(prescriptionId)

                if (prescription == null) {
                    Log.d("TAG", "Prescription is not found!")
                    return@launch
                }

                val taken = prescription.takenCount
                val total = prescription.totalDoses
                val percent = if (total > 0) (taken * 100) / total else 0
                val doctorFirstName = prescription.doctorFirstName
                val doctorLastName = prescription.doctorLastName

                val uiModel = PrescriptionUiModel(
                    prescription = prescription,
                    takenCount = taken,
                    totalCount = total,
                    progressPercent = percent,
                    isCompleted = taken >= total,
                    doctorName = "$doctorFirstName $doctorLastName"
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