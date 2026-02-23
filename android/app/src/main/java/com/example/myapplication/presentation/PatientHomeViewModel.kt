package com.example.myapplication.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Repository
import com.example.myapplication.domain.Prescription

class PatientHomeViewModel: ViewModel() {

    private val repository = Repository()
    private var _patients = MutableLiveData<List<Prescription>>()
    val patients get() = _patients


    init {
        _patients.value = repository.getTestMedicines()
    }

}