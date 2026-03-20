package com.example.myapplication.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DoctorRepository
import com.example.myapplication.domain.UserProfileUi
import com.example.myapplication.presentation.SessionManager.currentUser
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
        viewModelScope.launch {
            val doctor = repository.getDoctorProfile()
            if (doctor == null) {
                Log.e("TAG", "Doctor is not found!")
                return@launch
            }

            _userProfile.value = UserProfileUi(
                firstName = doctor.firstName,
                lastName = doctor.lastName,
                email = currentUser!!.email,
                roleLabel = "Doctor",
                initials = "${doctor.firstName.first()} ${doctor.lastName.first()}"
            )
        }
    }

}