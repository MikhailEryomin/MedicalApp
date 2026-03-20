package com.example.myapplication.domain

import java.util.Date

data class Prescription(
    val id: Int,
    val doctorId: Int,
    val patientId: Int,

    val medicine: Medicine,

    val dosage: String,

    val frequency: Int,

    val durationDays: Int,

    val startDate: String,
    val endDate: String,

    val status: PrescriptionStatus,
    val notes: String = "",

    val takenCount: Int = 0
) {
    val totalDoses: Int
        get() = frequency * durationDays
}

enum class PrescriptionStatus(val displayName: String) {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}
