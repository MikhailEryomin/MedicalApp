package com.example.myapplication.domain

import java.util.Date

data class Patient (
    val id: Int,
    val firstName: String,
    val lastName: String,
    val birthDate: Date,
    val gender: Gender,
    val allergies: List<String> = emptyList(),
    val chronicDiseases: List<String> = emptyList()
) {
    val age: Int
        get() {
            val now = Date()
            val diff = now.time - birthDate.time
            return (diff / (1000L * 60 * 60 * 24 * 365)).toInt()
        }
}

enum class Gender {
    MALE,
    FEMALE
}

