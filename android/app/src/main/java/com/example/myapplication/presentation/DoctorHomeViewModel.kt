package com.example.myapplication.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Repository
import com.example.myapplication.domain.Patient

class DoctorHomeViewModel: ViewModel() {

    private var _patients = MutableLiveData<List<Patient>>()
    val patients get() = _patients
    private val repository = Repository()

    init {
        _patients.value = repository.getTestPatients()
    }

}