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

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val prescriptions = repository.getMyPrescriptions()
                _isEmpty.value = prescriptions.isEmpty()

                val uiModels = prescriptions.map { prescription ->

                    val taken = prescription.takenCount
                    val total = prescription.totalDoses
                    val percent = if (total > 0) (taken * 100) / total else 0
                    val doctorName = "${prescription.doctorFirstName} ${prescription.doctorLastName}"


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

                getActivePrescriptions()

            } catch (e: Exception) {
                //handling
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onMarkTakenClicked(item: PrescriptionUiModel) {
        viewModelScope.launch {
            val currentList = _uiState.value.orEmpty().toMutableList()
            val index = currentList.indexOfFirst { it.prescription.id == item.prescription.id }

            if (index != -1) {
                currentList[index] = currentList[index].copy(isLoading = true)
                _uiState.value = currentList
            }

            // 2. ОТПРАВЛЯЕМ ЗАПРОС НА СЕРВЕР
            try {
                repository.incrementTakenCount(item.prescription.id)
                loadMyPrescriptions()
            } catch (e: Exception) {
                val revertedList = _uiState.value.orEmpty().toMutableList()
                val revertIndex = revertedList.indexOfFirst { it.prescription.id == item.prescription.id }
                if (revertIndex != -1) {
                    revertedList[revertIndex] = revertedList[revertIndex].copy(isLoading = false)
                    _uiState.value = revertedList
                }
            }
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