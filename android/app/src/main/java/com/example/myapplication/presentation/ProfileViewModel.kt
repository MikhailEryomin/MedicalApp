package com.example.myapplication.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AuthRepository
import com.example.myapplication.data.DoctorRepository
import com.example.myapplication.domain.User
import com.example.myapplication.domain.UserProfileUi
import com.example.myapplication.domain.UserRole
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {

    private val repository = DoctorRepository()

    private val _userProfile: MutableLiveData<UserProfileUi> = MutableLiveData()
    val userProfile get() = _userProfile

    private val _isLoggedOut = MutableLiveData(false)
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    fun logout() {
        SessionManager.clear()
        _isLoggedOut.value = true
    }

    init {
        loadUser()
    }

    fun loadUser() {
        val currentUser = SessionManager.currentUser ?: return
        val userId = currentUser.id

        viewModelScope.launch {
            if (currentUser.role == UserRole.DOCTOR) {
                val doctor = repository.getDoctorProfile(userId) ?: return@launch
                _userProfile.value = UserProfileUi(
                    firstName = doctor.firstName,
                    lastName = doctor.lastName,
                    email = currentUser.email,
                    roleLabel = "Doctor",
                    initials = "${doctor.firstName.first()} ${doctor.lastName.first()}"
                )
            } else {
                val patient = repository.getPatientProfile(userId) ?: return@launch
                _userProfile.value = UserProfileUi(
                    firstName = patient.firstName,
                    lastName = patient.lastName,
                    email = currentUser.email,
                    roleLabel = "Patient",
                    initials = "${patient.firstName.first()} ${patient.lastName.first()}"
                )
            }
        }
    }

}