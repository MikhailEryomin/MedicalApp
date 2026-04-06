package com.example.myapplication.domain

import androidx.annotation.PluralsRes
import com.example.myapplication.R


data class Medicine(
    val id: Int,
    val name: String,
    val form: MedicineForm,
    val defaultDosage: String
)

enum class MedicineForm(@PluralsRes val unitNameRes: Int) {
    TABLET(R.plurals.pills),
    SYRUP(R.plurals.doses),
    INJECTION(R.plurals.injections),
    OINTMENT(R.plurals.ointments)
}
