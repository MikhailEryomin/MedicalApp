package com.example.myapplication.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DoctorRepository
import com.example.myapplication.domain.Patient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class DoctorHomeTab { MY_PATIENTS, GLOBAL_SEARCH }

class DoctorHomeViewModel : ViewModel() {

    private val repository = DoctorRepository()

    private var myPatientsList: List<Patient> = emptyList()
    private var searchJob: Job? = null // Для задержки (debounce) при вводе текста

    private val _displayedPatients = MutableLiveData<List<Patient>>()
    val displayedPatients: LiveData<List<Patient>> = _displayedPatients

    private val _currentTab = MutableLiveData(DoctorHomeTab.MY_PATIENTS)
    val currentTab: LiveData<DoctorHomeTab> = _currentTab

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadMyPatients()
    }

    fun loadMyPatients() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                myPatientsList = repository.getPatientsForDoctor()
                if (_currentTab.value == DoctorHomeTab.MY_PATIENTS) {
                    _displayedPatients.value = myPatientsList
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setTab(tab: DoctorHomeTab, currentSearchQuery: String) {
        _currentTab.value = tab
        if (tab == DoctorHomeTab.MY_PATIENTS) {
            filterMyPatientsLocally(currentSearchQuery)
        } else {
            searchGlobalPatients(currentSearchQuery)
        }
    }

    fun onSearchQueryChanged(query: String) {
        if (_currentTab.value == DoctorHomeTab.MY_PATIENTS) {
            filterMyPatientsLocally(query)
        } else {
            searchGlobalPatients(query)
        }
    }

    private fun filterMyPatientsLocally(query: String) {
        val lowercase = query.lowercase()
        _displayedPatients.value = myPatientsList.filter {
            it.firstName.lowercase().contains(lowercase) || it.lastName.lowercase().contains(lowercase)
        }
    }

    private fun searchGlobalPatients(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _isLoading.value = true

            val results = repository.searchGlobalPatients(query)
            val filteredResults = results.filter { globalPatient ->
                myPatientsList.none { myPatient -> myPatient.id == globalPatient.id }
            }

            _displayedPatients.value = filteredResults
            _isLoading.value = false
        }
    }

    fun attachPatient(patientId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.attachPatientToDoctor(patientId)
            if (success) {
                // Скачиваем обновленный список своих пациентов
                loadMyPatients()
                // Возвращаем UI на вкладку "Мои пациенты"
                _currentTab.value = DoctorHomeTab.MY_PATIENTS
            } else {
                _isLoading.value = false
                // Тут можно было бы кинуть Toast об ошибке
            }
        }
    }
}