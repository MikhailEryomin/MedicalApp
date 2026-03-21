package com.example.myapplication.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AuthRepository
import com.example.myapplication.domain.UserRole
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {

    private val repository = AuthRepository()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _loginSuccess = MutableLiveData<UserRole?>()
    val loginSuccess: LiveData<UserRole?> = _loginSuccess

    private val _selectedRole = MutableLiveData(UserRole.PATIENT)
    val selectedRole: LiveData<UserRole> = _selectedRole

    fun selectRole(role: UserRole) {
        _selectedRole.value = role
    }

    fun resetState() {
        _errorMessage.value = null
        _isLoading.value = false
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Заполните все поля"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.login(email, pass, selectedRole.value!!)

            result.onSuccess { user ->
                SessionManager.saveUser(user)
                _loginSuccess.value = user.role
            }.onFailure { error ->
                _errorMessage.value = error.message
            }

            _isLoading.value = false
        }
    }

    fun onNavigationComplete() {
        _loginSuccess.value = null
    }

}