package com.example.myapplication.data

import com.example.myapplication.domain.Prescription
import com.example.myapplication.domain.Patient

class Repository {

    fun getTestPatients(): List<Patient> {
        return listOf(
            Patient(0, "Andrew Garfield", 34),
            Patient(1, "Tobey Maguire", 45),
            Patient(2, "Donald Trump", 60)
        )
    }

    fun getTestMedicines(): List<Prescription> {
        return listOf(
            Prescription(0, "Lisobact", 20, 500, "Doctor A.B"),
            Prescription(1, "Strepsilse", 45, 200, "Doctor E.C"),
            Prescription(2, "Tantum Verde", 60, 300, "Doctor J.W")
        )
    }

}