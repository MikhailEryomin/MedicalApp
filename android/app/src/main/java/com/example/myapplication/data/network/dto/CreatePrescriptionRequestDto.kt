package com.example.myapplication.data.network.dto

import com.google.gson.annotations.SerializedName

data class CreatePrescriptionRequestDto(
    @SerializedName("patientId") val patientId: Int,
    @SerializedName("medicineId") val medicineId: Int,
    @SerializedName("dosage") val dosage: String,
    @SerializedName("frequency") val frequency: Int,
    @SerializedName("durationDays") val durationDays: Int,
    @SerializedName("notes") val notes: String
)