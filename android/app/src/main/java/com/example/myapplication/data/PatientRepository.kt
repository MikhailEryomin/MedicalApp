package com.example.myapplication.data

import com.example.myapplication.domain.Doctor
import com.example.myapplication.domain.Patient
import com.example.myapplication.domain.Prescription
import com.example.myapplication.domain.PrescriptionStatus
import kotlinx.coroutines.delay
import java.util.Date


class PatientRepository {

    private val doctorRepo = DoctorRepository()

    suspend fun getMyPrescriptions(patientId: Int): List<Prescription> {
        return doctorRepo.getPrescriptionsByPatient(patientId)
    }

    suspend fun getActivePrescriptions(patientId: Int): List<Prescription> {
        return getMyPrescriptions(patientId)
            .filter { it.status == PrescriptionStatus.ACTIVE }
    }

    suspend fun getCompletedPrescriptions(patientId: Int): List<Prescription> {
        return getMyPrescriptions(patientId)
            .filter { it.status == PrescriptionStatus.COMPLETED }
    }

    suspend fun getPrescriptionById(patientId: Int, prescriptionId: Int): Prescription? {
        delay(200)
        // TODO: GET /api/prescriptions/{id}
        return getMyPrescriptions(patientId).find { it.id == prescriptionId } // patientId hardcoded пока
    }

    suspend fun incrementTakenCount(prescriptionId: Int) {
        doctorRepo.incrementTakenCount(prescriptionId)
    }

    fun getDoctorById(doctorId: Int): Doctor? {
        return doctorRepo.getDoctorById(doctorId)
    }

    suspend fun getPatientProfile(): Patient? {
        return doctorRepo.getPatientProfile()
    }

}