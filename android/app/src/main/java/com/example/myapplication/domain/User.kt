package com.example.myapplication.domain

data class User (
    val id: Int,
    val userId: Int = 0,
    val email: String,
    val role: UserRole,
    val token: String = ""
)

enum class UserRole(val displayName: String) {
    DOCTOR("Доктор"),
    PATIENT("Пациент")
}