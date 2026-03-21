package com.example.myapplication.presentation

import com.example.myapplication.domain.User
import com.example.myapplication.domain.UserRole

object SessionManager {

    var currentUser: User? = null
        private set

    fun saveUser(user: User) {
        currentUser = user
    }

    fun clear() {
        currentUser = null
    }

}