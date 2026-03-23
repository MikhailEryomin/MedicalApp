package com.example.myapplication.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.SessionManager
import com.example.myapplication.data.AuthRepository
import com.example.myapplication.data.DoctorRepository
import com.example.myapplication.domain.Gender
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class RegisterPatientViewModel : ViewModel() {

    private val authRepository = AuthRepository()

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

    fun createPatient(
        firstName: String,
        lastName: String,
        birthDate: String,
        email: String,
        password: String
    ) {

        if (!validateInput(firstName, lastName, birthDate, email, password)) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Вызываем новый метод из AuthRepository
                val result = authRepository.registerPatient(
                    firstName = firstName,
                    lastName = lastName,
                    birthDate = birthDate,
                    gender = selectedGender.value!!.name, // "MALE" или "FEMALE"
                    email = email,
                    password = password,
                    allergies = _allergies.value.orEmpty(),
                    diseases = _diseases.value.orEmpty()
                )

                result.onSuccess { user ->
                    SessionManager.saveUser(user) // Сразу логиним пользователя!
                    _isSuccess.value = true
                }.onFailure { error ->
                    _errorMessage.value = error.message
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateInput(
        firstName: String,
        lastName: String,
        birthDate: String,
        email: String,
        password: String
    ): Boolean {
        if (firstName.isBlank() || lastName.isBlank()) {
            _errorMessage.value = "Пожалуйста, введите имя и фамилию"
            return false
        }
        val dateRegex = Regex("""^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[012])\.(19|20)\d\d$""")
        if (birthDate.isEmpty() || !birthDate.matches(dateRegex)) {
            _errorMessage.value = "Пожалуйста, укажите дату рождения"
            return false
        }
        val regex = "[a-zA-Z]+\\d*@[a-zA-Z]+\\.[a-z]+".toRegex()
        if (email.isBlank() || !email.matches(regex)) {
            _errorMessage.value = "Пожалуйста, проверьте ваш email"
            return false
        }
        if (password.length < 6) {
            _errorMessage.value = "Пароль должен содержать как минимум 6 символов"
            return false
        }
        return true
    }


}