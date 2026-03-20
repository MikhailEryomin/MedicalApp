package com.example.myapplication.data.network.dto

import com.example.myapplication.domain.Doctor
import com.google.gson.annotations.SerializedName

data class DoctorDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("specialization") val specialization: String?,
    @SerializedName("licenceNumber") val licenceNumber: String?
)

fun DoctorDto.toDomain(): Doctor {
    return Doctor(
        id = this.id,
        userId = this.userId,
        firstName = this.firstName,
        lastName = this.lastName,
        specialization = this.specialization ?: "",
        licenceNumber = this.licenceNumber ?: ""
    )
}