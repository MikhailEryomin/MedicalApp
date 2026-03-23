package com.example.myapplication.domain

data class PrescriptionUiModel(
    val prescription: Prescription,
    val takenCount: Int,
    val totalCount: Int,
    val progressPercent: Int,
    val isCompleted: Boolean,
    val doctorName: String,
    val isLoading: Boolean = false
)