package com.example.myapplication.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.SessionManager
import com.example.myapplication.data.DoctorRepository
import com.example.myapplication.domain.UserProfileUi
import com.example.myapplication.domain.UserRole
import com.example.myapplication.SessionManager.currentUser
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {

    private val repository = DoctorRepository()

    private val _userProfile: MutableLiveData<UserProfileUi> = MutableLiveData()
    val userProfile get() = _userProfile

    private val _isLoggedOut = MutableLiveData(false)
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    fun logout() {
        SessionManager.clear()
    }

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            val currentUser = currentUser ?: return@launch

            val profile = when (currentUser.role) {
                UserRole.DOCTOR -> {
                    val doctor = repository.getDoctorProfile()
                    createUserProfile(
                        firstName = doctor.firstName,
                        lastName = doctor.lastName,
                        email = currentUser.email,
                        roleLabel = UserRole.DOCTOR.displayName
                    )
                }
                else -> {
                    val patient = repository.getPatientProfile()
                    createUserProfile(
                        firstName = patient.firstName,
                        lastName = patient.lastName,
                        email = currentUser.email,
                        roleLabel = UserRole.PATIENT.displayName
                    )
                }
            }

            _userProfile.value = profile
        }
    }

    private fun createUserProfile(
        firstName: String,
        lastName: String,
        email: String,
        roleLabel: String
    ): UserProfileUi {
        return UserProfileUi(
            firstName = firstName,
            lastName = lastName,
            email = email,
            roleLabel = roleLabel,
            initials = "${firstName.first()} ${lastName.first()}"
        )
    }

}