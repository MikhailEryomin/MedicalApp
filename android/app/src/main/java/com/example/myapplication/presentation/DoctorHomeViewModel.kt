package com.example.myapplication.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DoctorRepository
import com.example.myapplication.domain.Patient
import kotlinx.coroutines.launch

class DoctorHomeViewModel: ViewModel() {

    private val repository = DoctorRepository()

    private var allPatients: List<Patient> = emptyList()

    private var _patients = MutableLiveData<List<Patient>>()
    val patients get() = _patients

    init {
        loadMyPatients()
    }

    fun loadMyPatients() {
        viewModelScope.launch {
            allPatients = repository.getPatientsForDoctor()
            _patients.value = allPatients
        }
    }

    fun searchPatients(query: String) {
        val lowercase = query.lowercase()
        val result = allPatients.filter { patient ->
            patient.firstName.lowercase().contains(lowercase) ||
                    patient.lastName.contains(lowercase)
        }
        _patients.value = result
    }

}