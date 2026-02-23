package com.example.myapplication.domain

data class Prescription(
    val id: Int,
    val name: String,
    val pillsCount: Int,
    val amountInMg: Int,
    val doctorName: String
)
