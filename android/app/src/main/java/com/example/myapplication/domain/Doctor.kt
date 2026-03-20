package com.example.myapplication.domain

data class Doctor (
    val id: Int,
    val firstName: String,
    val lastName: String,
    val specialization: String,
    val licenceNumber: String
)