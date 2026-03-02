package com.example.myapplication.domain

data class Medicine(
    val id: Int,
    val name: String,
    val form: MedicineForm,
    val defaultDosage: String
)

enum class MedicineForm {
    TABLET,    // Таблетки
    SYRUP,     // Сироп
    INJECTION, // Уколы
    OINTMENT   // Мазь
}
