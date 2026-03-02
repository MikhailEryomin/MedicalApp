package com.example.myapplication.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.PatientRepository
import com.example.myapplication.domain.PrescriptionStatus
import com.example.myapplication.domain.PrescriptionUiModel
import kotlinx.coroutines.launch

class PatientHomeViewModel : ViewModel() {

    private val repository = PatientRepository()

    private var allPrescriptions: List<PrescriptionUiModel> = emptyList()

    private val _uiState = MutableLiveData<List<PrescriptionUiModel>>()
    val uiState: LiveData<List<PrescriptionUiModel>> = _uiState

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isEmpty = MutableLiveData(false)
    val isEmpty: LiveData<Boolean> = _isEmpty

    init {
        loadMyPrescriptions()
    }

    private fun loadMyPrescriptions() {

        val currentUser = SessionManager.currentUser ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val patientProfile = repository.getPatientProfile(currentUser.id)
                val prescriptions = repository.getMyPrescriptions(patientProfile!!.id)
                _isEmpty.value = prescriptions.isEmpty()

                val uiModels = prescriptions.map { prescription ->

                    val taken = repository.getTakenCount(prescription.id)
                    val total = prescription.totalDoses
                    val percent = if (total > 0) (taken * 100) / total else 0
                    val doctor = repository.getDoctorById(prescription.doctorId)

                    val doctorName =
                        if (doctor != null) "${doctor.firstName} ${doctor.lastName}" else ""

                    PrescriptionUiModel(
                        prescription = prescription,
                        takenCount = taken,
                        totalCount = total,
                        progressPercent = percent,
                        isCompleted = taken >= total,
                        doctorName = doctorName
                    )
                }

                allPrescriptions = uiModels

                _uiState.value = uiModels

            } catch (e: Exception) {
                //handling
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onMarkTakenClicked(item: PrescriptionUiModel) {
        viewModelScope.launch {
            repository.incrementTakenCount(item.prescription.id)
            loadMyPrescriptions()
        }
    }

    fun getActivePrescriptions() {
        val result = allPrescriptions.filter { item ->
            item.prescription.status == PrescriptionStatus.ACTIVE
        }
        _uiState.value = result
    }

    fun getCompletedPrescriptions() {
        val result = allPrescriptions.filter { item ->
            item.prescription.status == PrescriptionStatus.COMPLETED
        }
        _uiState.value = result
    }

}