package com.example.myapplication.data.network.dto

import com.example.myapplication.domain.User
import com.example.myapplication.domain.UserRole
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("role") val role: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,

    // Doctor
    @SerializedName("specialization") val specialization: String?,
    @SerializedName("licenceNumber") val licenceNumber: String?,

    // Patient
    @SerializedName("birthDate") val birthDate: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("allergies") val allergies: List<String>?,
    @SerializedName("chronicDiseases") val chronicDiseases: List<String>?
)

fun UserDto.toDomain(email: String, token: String): User {
    val domainRole = try {
        UserRole.valueOf(this.role.uppercase())
    } catch (e: Exception) {
        UserRole.PATIENT
    }

    return User(
        id = this.id,
        email = email,
        role = domainRole,
        token = token,
    )
}