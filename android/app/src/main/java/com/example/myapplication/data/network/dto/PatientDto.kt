package com.example.myapplication.data.network.dto

import com.example.myapplication.domain.Gender
import com.example.myapplication.domain.Patient
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PatientDto(
    @SerializedName("id") val id: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("birthDate") val birthDate: String, // Придет строка "YYYY-MM-DD"
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
    @SerializedName("allergies") val allergies: List<String>?,
    @SerializedName("chronicDiseases") val chronicDiseases: List<String>?
)

fun PatientDto.toDomain(): Patient {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val parsedDate = try {
        format.parse(this.birthDate) ?: Date()
    } catch (e: Exception) {
        Date()
    }

    return Patient(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        birthDate = parsedDate,
        gender = if (this.gender.equals("FEMALE", ignoreCase = true)) Gender.FEMALE else Gender.MALE,
        allergies = this.allergies ?: emptyList(),
        chronicDiseases = this.chronicDiseases ?: emptyList()
    )
}