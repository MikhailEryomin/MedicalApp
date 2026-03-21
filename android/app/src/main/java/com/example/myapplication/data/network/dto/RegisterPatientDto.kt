package com.example.myapplication.data.network.dto

import com.google.gson.annotations.SerializedName

data class RegisterPatientDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("birthDate") val birthDate: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("allergies") val allergies: List<String>,
    @SerializedName("chronicDiseases") val diseases: List<String>
)