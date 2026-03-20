package com.example.myapplication.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DoctorRepository
import com.example.myapplication.domain.Medicine
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class CreatePrescriptionViewModel: ViewModel() {

    private val doctorRepository = DoctorRepository()

    private val _selectedMedicine = MutableLiveData<Medicine?>()
    val selectedMedicine: LiveData<Medicine?> = _selectedMedicine

    private val _medicineSuggestions = MutableLiveData<List<Medicine>>()
    val medicineSuggestions: LiveData<List<Medicine>> = _medicineSuggestions

    private val _frequency = MutableLiveData(0)
    val frequency: LiveData<Int> = _frequency

    private val _duration = MutableLiveData(10)
    val duration: LiveData<Int> = _duration

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun incFrequencyValue() {
        _frequency.value = _frequency.value?.plus(1)
    }

    fun decFrequencyValue() {
        _frequency.value = _frequency.value?.minus(1)
    }

    fun incDurationValue() {
        _duration.value = _duration.value?.plus(1)
    }

    fun decDurationValue() {
        _duration.value = _duration.value?.minus(1)
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            if (query.length < 2) {
                _medicineSuggestions.value = emptyList()
                return@launch
            }
            val results = doctorRepository.searchMedicines(query)
            _medicineSuggestions.value = results
        }
    }

    fun selectMedicine(medicine: Medicine) {
        _selectedMedicine.value = medicine
        _medicineSuggestions.value = emptyList()
    }

    fun savePrescription(
        patientId: Int,
        dosage: String,
        frequency: Int,
        durationStr: String,
        notes: String
    ) {
        val medicine = _selectedMedicine.value

        // Валидация
        if (medicine == null) {
            _errorMessage.value = "Выберите лекарство из списка"
            return
        }
        if (dosage.isBlank()) {
            _errorMessage.value = "Укажите дозировку"
            return
        }
        val duration = durationStr.toIntOrNull()
        if (duration == null || duration <= 0) {
            _errorMessage.value = "Укажите корректную длительность (дней)"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {

                doctorRepository.createPrescription(
                    patientId = patientId,
                    medicineId = medicine.id,
                    dosage = dosage,
                    frequency = frequency,
                    durationDays = duration,
                    notes = notes
                )

                _isSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


}