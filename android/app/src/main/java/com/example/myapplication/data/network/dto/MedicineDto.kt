package com.example.myapplication.data.network.dto

import com.example.myapplication.domain.Medicine
import com.example.myapplication.domain.MedicineForm
import com.google.gson.annotations.SerializedName

data class MedicineDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("form") val form: String,
    @SerializedName("default_dosage") val defaultDosage: String?
)

fun MedicineDto.toDomain(): Medicine {
    val domainForm = try {
        MedicineForm.valueOf(this.form.uppercase())
    } catch (e: Exception) {
        MedicineForm.TABLET // Фолбэк, если сервер пришлет что-то странное
    }

    return Medicine(
        id = this.id,
        name = this.name,
        form = domainForm,
        defaultDosage = this.defaultDosage ?: ""
    )
}