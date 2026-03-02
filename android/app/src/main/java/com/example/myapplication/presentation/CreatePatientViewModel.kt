package com.example.myapplication.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DoctorRepository
import com.example.myapplication.domain.Gender
import kotlinx.coroutines.launch

class CreatePatientViewModel: ViewModel() {

    private val doctorRepository = DoctorRepository()

    private val _selectedGender = MutableLiveData(Gender.MALE)
    val selectedGender: LiveData<Gender> = _selectedGender

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _allergies = MutableLiveData<List<String>>(emptyList())
    val allergies: LiveData<List<String>> = _allergies

    private val _diseases = MutableLiveData<List<String>>(emptyList())
    val diseases: LiveData<List<String>> = _diseases


    fun setGender(gender: Gender) {
        _selectedGender.value = gender
    }

    fun addAllergy(item: String) {
        if (item.isBlank()) return

        val currentList = _allergies.value.orEmpty().toMutableList()
        if (!currentList.contains(item)) {
            currentList.add(item)
            _allergies.value = currentList
        }
    }

    fun removeAllergy(item: String) {
        val currentList = _allergies.value.orEmpty().toMutableList()
        currentList.remove(item)
        _allergies.value = currentList
    }

    fun addDisease(item: String) {
        if (item.isBlank()) return

        val currentList = _diseases.value.orEmpty().toMutableList()
        if (!currentList.contains(item)) {
            currentList.add(item)
            _diseases.value = currentList
        }
    }

    fun removeDisease(item: String) {
        val currentList = _diseases.value.orEmpty().toMutableList()
        currentList.remove(item)
        _diseases.value = currentList
    }

    fun createPatient(firstName: String, lastName: String, birthDate: String, email: String) {

        if (!validateInput(firstName, lastName, birthDate, email)) return

        val dateParts = birthDate.split(".")
        val day = dateParts[0].toInt()
        val month = dateParts[1].toInt()
        val year = dateParts[2].toInt()

        viewModelScope.launch {
            _isLoading.value = true
            try {

                val currentUser = SessionManager.currentUser ?: return@launch
                val doctorProfile =
                    doctorRepository.getDoctorProfile(currentUser.id) ?: return@launch

                doctorRepository.createPatient(
                    doctorId = doctorProfile.id,
                    firstName = firstName,
                    lastName = lastName,
                    birthDate = DoctorRepository.createDate(year, month, day),
                    email = email,
                    gender = selectedGender.value!!,
                    allergies = _allergies.value.orEmpty(),
                    diseases = _diseases.value.orEmpty()
                )

                _isSuccess.value = true

            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }

    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun validateInput(firstName: String, lastName: String, birthDate: String, email: String): Boolean {
        if (firstName.isBlank() || lastName.isBlank()) {
            _errorMessage.value = "Please enter first name and last name"
            return false
        }
        val dateRegex = Regex("""^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[012])\.(19|20)\d\d$""")
        if (birthDate.isEmpty() || !birthDate.matches(dateRegex)) {
            _errorMessage.value = "Please select date of birth"
            return false
        }
        val regex = "[a-zA-Z]+\\d*@[a-zA-Z]+\\.[a-z]+".toRegex()
        if (email.isBlank() || !email.matches(regex)) {
            _errorMessage.value = "Please enter a valid email"
            return false
        }
        return true
    }


}