package com.example.myapplication.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DoctorRepository
import com.example.myapplication.domain.Patient
import com.example.myapplication.domain.Prescription
import kotlinx.coroutines.launch

class PatientDetailsViewModel: ViewModel() {

    private val repository = DoctorRepository()
    private var patientId: Int = 0

    private val _patient = MutableLiveData<Patient>()
    val patient: LiveData<Patient> = _patient

    private val _prescriptions = MutableLiveData<List<Prescription>>()
    val prescriptions: LiveData<List<Prescription>> = _prescriptions

    private val _isGeneralLoading = MutableLiveData<Boolean>()
    val isGeneralLoading: LiveData<Boolean> = _isGeneralLoading

    private val _isPrescriptionsLoading = MutableLiveData<Boolean>()
    val isPrescriptionsLoading: LiveData<Boolean> = _isPrescriptionsLoading


    fun loadPatient(patientId: Int) {
        viewModelScope.launch {
            _isGeneralLoading.value = true
            try {
                val patientData = repository.getPatientById(patientId)

                if (patientData == null) {
                    Log.d("TAG", "PatientData is not found")
                    return@launch
                }

                _patient.value = patientData
            } catch (e: Exception) {
                // Обработка сетевых ошибок
            } finally {
                _isGeneralLoading.value = false
            }
        }
    }

    fun loadPrescriptions(patientId: Int) {
        viewModelScope.launch {
            _isPrescriptionsLoading.value = true
            try {
                val prescriptions = repository.getPrescriptionsByPatient(patientId)
                _prescriptions.value = prescriptions
            } catch (e: Exception) {
                Log.e("TAG", e.message.toString())
            } finally {
                _isPrescriptionsLoading.value = false
            }
        }
    }

}