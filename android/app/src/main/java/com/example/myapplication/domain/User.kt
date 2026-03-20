package com.example.myapplication.domain

data class User (
    val id: Int,
    val email: String,
    val role: UserRole,
    val token: String = ""
)

enum class UserRole() {
    DOCTOR,
    PATIENT
}