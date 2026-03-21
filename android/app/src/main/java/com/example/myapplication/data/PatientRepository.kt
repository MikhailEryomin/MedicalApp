package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.data.network.RetrofitClient
import com.example.myapplication.data.network.dto.toDomain
import com.example.myapplication.domain.Doctor
import com.example.myapplication.domain.Patient
import com.example.myapplication.domain.Prescription
import com.example.myapplication.domain.PrescriptionStatus


class PatientRepository {

    private val doctorRepo = DoctorRepository()

    suspend fun getMyPrescriptions(): List<Prescription> {

        val patientProfile = getPatientProfile()

        return try {
            val prescriptionDtos =
                RetrofitClient.doctorApi.getPatientPrescriptions(patientProfile.id)
            prescriptionDtos.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Ошибка загрузки рецептов: ${e.message}")
            emptyList()
        }
    }

    suspend fun getActivePrescriptions(patientId: Int): List<Prescription> {
        return getMyPrescriptions()
            .filter { it.status == PrescriptionStatus.ACTIVE }
    }

    suspend fun getCompletedPrescriptions(patientId: Int): List<Prescription> {
        return getMyPrescriptions()
            .filter { it.status == PrescriptionStatus.COMPLETED }
    }

    suspend fun getMyPrescriptionById(prescriptionId: Int): Prescription? {
        return getMyPrescriptions().find { it.id == prescriptionId }
    }

    suspend fun incrementTakenCount(prescriptionId: Int) {
        doctorRepo.incrementTakenCount(prescriptionId)
    }

    fun getDoctorById(doctorId: Int): Doctor? {
        return doctorRepo.getDoctorById(doctorId)
    }

    suspend fun getPatientProfile(): Patient {
        return doctorRepo.getPatientProfile()
    }

}