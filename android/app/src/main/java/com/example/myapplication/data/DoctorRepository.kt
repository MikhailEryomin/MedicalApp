package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.data.network.RetrofitClient
import com.example.myapplication.data.network.dto.CreatePatientRequestDto
import com.example.myapplication.data.network.dto.CreatePrescriptionRequestDto
import com.example.myapplication.data.network.dto.toDomain
import com.example.myapplication.domain.*
import com.example.myapplication.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class DoctorRepository {

    suspend fun searchGlobalPatients(query: String): List<Patient> {
        return try {
            val patientDtos = RetrofitClient.doctorApi.searchAllPatients(query)
            patientDtos.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Ошибка глобального поиска: ${e.message}")
            emptyList()
        }
    }

    suspend fun attachPatientToDoctor(patientId: Int): Boolean {
        return try {
            RetrofitClient.doctorApi.attachPatient(patientId)
            Log.d("DoctorRepository", "Пациент $patientId успешно прикреплен!")
            true
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Ошибка прикрепления: ${e.message}")
            false
        }
    }

    suspend fun detachPatientFromDoctor(patientId: Int): Boolean {
        return try {
            RetrofitClient.doctorApi.detachPatient(patientId)
            Log.d("DoctorRepository", "Пациент $patientId успешно откреплен!")
            true
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Ошибка открепления: ${e.message}")
            false
        }
    }

    suspend fun getPatientProfile(): Patient {
        val dto = RetrofitClient.authApi.getUserInfo()
        return Patient(
            id = dto.id,
            firstName = dto.firstName,
            lastName = dto.lastName,
            birthDate = createDateByString(dto.birthDate!!),
            gender = if(dto.gender == "MALE") Gender.MALE else Gender.FEMALE,
            allergies = dto.allergies ?: emptyList(),
            chronicDiseases = dto.chronicDiseases ?: emptyList()
        )
    }

    suspend fun getDoctorProfile(): Doctor {
        val dto = RetrofitClient.authApi.getUserInfo()
        return Doctor(
            id = dto.id,
            firstName = dto.firstName,
            lastName = dto.lastName,
            specialization = dto.specialization ?: "",
            licenceNumber = dto.licenceNumber ?: ""
        )
    }

    suspend fun getPatientsForDoctor(): List<Patient> {
        return try {
            val patientDtos = RetrofitClient.doctorApi.getMyPatients()

            val patients = patientDtos.map { it.toDomain() }

            Log.d("DoctorRepository", "Успешно загружено пациентов: ${patients.size}")
            patients
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Ошибка загрузки пациентов: ${e.message}")
            emptyList()
        }
    }

    suspend fun getPatientById(id: Int): Patient? {
        return try {
            val patientDto = RetrofitClient.doctorApi.getPatientById(id)
            patientDto.toDomain()
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Ошибка загрузки профиля пациента: ${e.message}")
            null
        }
    }

    suspend fun createPatient(
        firstName: String,
        lastName: String,
        birthDateStr: String,
        email: String,
        password: String, //хз...
        gender: Gender,
        allergies: List<String>,
        diseases: List<String>
    ): Patient {

        val requestBody = CreatePatientRequestDto(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            birthDateStr = birthDateStr,
            gender = gender.name,
            allergies = allergies,
            chronicDiseases = diseases
        )

        val patientDto = RetrofitClient.doctorApi.createPatient(requestBody)

        Log.d("DoctorRepository", "Пациент успешно создан! ID: ${patientDto.id}")
        return patientDto.toDomain()
    }

    suspend fun getPrescriptionsByPatient(patientId: Int): List<Prescription> {
        return try {
            val prescriptionDtos = RetrofitClient.doctorApi.getPatientPrescriptions(patientId)
            prescriptionDtos.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Ошибка загрузки рецептов: ${e.message}")
            emptyList()
        }
    }

    suspend fun searchMedicines(query: String): List<Medicine> {
//        if (query.isEmpty()) return emptyList()
//        val medicineDto = RetrofitClient.doctorApi.searchMedicines(query)
//        return medicineDto.map { it.toDomain() }
        return try {
            val medicineDtos = RetrofitClient.doctorApi.searchMedicines(query)
            medicineDtos.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Ошибка поиска лекарств: ${e.message}")
            emptyList()
        }
    }

    suspend fun createPrescription(
        patientId: Int,
        medicineId: Int,
        dosage: String,
        frequency: Int,
        durationDays: Int,
        notes: String
    ): Prescription {

        val doctorProfile = getDoctorProfile()
        if (doctorProfile == null) {
            Log.e("TAG", "Doctor is not found!")
        }

        val doctorFirstName = doctorProfile?.firstName ?: ""
        val doctorLastName = doctorProfile?.lastName ?: ""

        val requestBody = CreatePrescriptionRequestDto(
            patientId = patientId,
            medicineId = medicineId,
            doctorFirstName = doctorFirstName,
            doctorLastName = doctorLastName,
            dosage = dosage,
            frequency = frequency,
            durationDays = durationDays,
            notes = notes
        )

        val prescriptionDto = RetrofitClient.doctorApi.createPrescription(requestBody)

        Log.d("DoctorRepository", "Рецепт успешно создан! ID: ${prescriptionDto.id}")
        return prescriptionDto.toDomain()
    }

    fun getDoctorById(doctorId: Int): Doctor? {
        return mockDoctors.find { it.id == doctorId }
    }


    suspend fun incrementTakenCount(prescriptionId: Int) {
        RetrofitClient.patientApi.markAsTaken(prescriptionId)
    }



    companion object {

        //mock data
        private val mockMedicines = listOf(
            Medicine(1, "Ибупрофен", MedicineForm.TABLET, "400 мг"),
            Medicine(2, "Амоксициллин", MedicineForm.TABLET, "500 мг"),
            Medicine(3, "Парацетамол", MedicineForm.SYRUP, "120 мг/5мл"),
            Medicine(4, "Цефтриаксон", MedicineForm.INJECTION, "1 г"),
            Medicine(5, "Левомеколь", MedicineForm.OINTMENT, "40 г")
        )

        private val mockDoctors = listOf<Doctor>(
            Doctor(
                id = 1, //doctorId
                firstName = "Mikhail",
                lastName = "Zadornov",
                specialization = "Dentist",
                licenceNumber = "2312312321"
            ),
            Doctor(
                id = 2, //doctorId
                firstName = "Alexey",
                lastName = "Shevcov",
                specialization = "Cardiolog",
                licenceNumber = "26433443"
            )

        )


        fun parseDateString(input: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val outputFormat = SimpleDateFormat("dd.MM.yyyy")
            val date = inputFormat.parse(input)
            val outputDate = outputFormat.format(date!!)
            return outputDate
        }

        fun createDateByString(input: String): Date {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            return formatter.parse(input)!!
        }

        //utils
        fun createDate(year: Int, month: Int, day: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, day)
            return calendar.time
        }

        fun formatDate(date: Date): String {
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            return formatter.format(date)
        }

        private fun addDays(date: Date, days: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DAY_OF_YEAR, days)
            return calendar.time
        }
    }


}