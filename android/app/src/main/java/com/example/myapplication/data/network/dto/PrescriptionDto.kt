package com.example.myapplication.data.network.dto

import com.example.myapplication.domain.Medicine
import com.example.myapplication.domain.MedicineForm
import com.example.myapplication.domain.Prescription
import com.example.myapplication.domain.PrescriptionStatus
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

data class PrescriptionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("doctorId") val doctorId: Int,
    @SerializedName("patientId") val patientId: Int,

    @SerializedName("medicineId") val medicineId: Int,
    @SerializedName("medicineName") val medicineName: String,

    @SerializedName("dosage") val dosage: String?,
    @SerializedName("frequency") val frequency: Int?,
    @SerializedName("durationDays") val durationDays: Int?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("notes") val notes: String?,

    @SerializedName("takenCount") val takenCount: Int?,
    @SerializedName("totalCount") val totalCount: Int?
)

fun PrescriptionDto.toDomain(): Prescription {
    val domainStatus = try {
        PrescriptionStatus.valueOf(this.status?.uppercase() ?: "ACTIVE")
    } catch (e: Exception) {
        PrescriptionStatus.ACTIVE
    }

    val serverFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val uiFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    val formattedStart = try {
        val date = serverFormat.parse(this.startDate ?: "")
        if (date != null) uiFormat.format(date) else (this.startDate ?: "")
    } catch (e: Exception) { this.startDate ?: "" }

    val formattedEnd = try {
        val date = serverFormat.parse(this.endDate ?: "")
        if (date != null) uiFormat.format(date) else (this.endDate ?: "")
    } catch (e: Exception) { this.endDate ?: "" }

    // Так как бэкенд не присылает form, ставим заглушку TABLET, на UI это не критично.
    val domainMedicine = Medicine(
        id = this.medicineId,
        name = this.medicineName,
        form = MedicineForm.TABLET,
        defaultDosage = this.dosage ?: ""
    )

    return Prescription(
        id = this.id,
        doctorId = this.doctorId,
        patientId = this.patientId,
        medicine = domainMedicine,
        dosage = this.dosage ?: "",
        frequency = this.frequency ?: 1,
        durationDays = this.durationDays ?: 1,
        startDate = formattedStart,
        endDate = formattedEnd,
        status = domainStatus,
        notes = this.notes ?: ""
    )
}