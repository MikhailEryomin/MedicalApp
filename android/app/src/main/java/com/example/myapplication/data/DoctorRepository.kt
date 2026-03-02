package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.domain.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class DoctorRepository {

    private val authRepository = AuthRepository()

    suspend fun getPatientProfile(userId: Int): Patient? {
        delay(100)
        return mockPatients.find { it.userId == userId }
    }

    suspend fun getDoctorProfile(userId: Int): Doctor? {
        delay(100)
        return mockDoctors.find { it.userId == userId }
    }

    suspend fun getPatientsForDoctor(currentDoctorId: Int): List<Patient> {
        delay(300)
        return mockPatients.filter { it.doctorId == currentDoctorId }
    }

    suspend fun getPatientById(id: Int): Patient? {
        delay(500)
        // TODO: GET /api/patients/{id}
        return mockPatients.find { it.id == id }
    }

    suspend fun createPatient(
        doctorId: Int,
        firstName: String,
        lastName: String,
        birthDate: Date,
        email: String,
        gender: Gender,
        allergies: List<String>,
        diseases: List<String>
    ): Patient {
        delay(400)
        // TODO: POST /api/patients

        val newId = (mockPatients.maxOfOrNull { it.id } ?: 0) + 1
        val newUser = authRepository.register(email, UserRole.PATIENT)

        val newPatient = Patient(
            id = newId,
            userId = newUser.id,
            doctorId = doctorId,
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            email = email,
            gender = gender,
            allergies = allergies,
            chronicDiseases = diseases
        )


        mockPatients.add(newPatient)
        Log.d("TAG", mockPatients.toString())
        return newPatient
    }

    suspend fun getPrescriptionsByPatient(patientId: Int): List<Prescription> {
        delay(1000)
        // TODO: GET /api/patients/{id}/prescriptions
        return mockPrescriptions.filter { it.patientId == patientId }
    }

    suspend fun searchMedicines(query: String): List<Medicine> {
        delay(150)
        // TODO: GET /api/medicines?search={query}
        return if (query.isEmpty()) {
            mockMedicines
        } else {
            mockMedicines.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }

    suspend fun createPrescription(
        doctorId: Int,
        patientId: Int,
        medicineId: Int,
        dosage: String,
        frequency: Int,
        durationDays: Int,
        notes: String
    ): Prescription {
        delay(400)
        // TODO: POST /api/prescriptions

        val medicine = mockMedicines.find { it.id == medicineId }
            ?: throw IllegalArgumentException("Medicine not found")

        val startDate = Date()
        val formattedStartDate = formatDate(startDate)
        val prescription = Prescription(
            id = mockPrescriptions.size + 1,
            doctorId = doctorId,
            patientId = patientId,
            medicine = medicine,
            dosage = dosage,
            frequency = frequency,
            durationDays = durationDays,
            startDate = formattedStartDate,
            endDate = formatDate(addDays(startDate, durationDays)),
            status = PrescriptionStatus.ACTIVE,
            notes = notes
        )

        mockPrescriptions.add(prescription)
        return prescription
    }

    fun getDoctorById(doctorId: Int): Doctor? {
        return mockDoctors.find { it.id == doctorId }
    }


    suspend fun incrementTakenCount(prescriptionId: Int) {
        delay(100)
        val current = mockLogs.getOrDefault(prescriptionId, 0) + 1
        mockLogs[prescriptionId] = current

        val index = mockPrescriptions.indexOfFirst { it.id == prescriptionId }
        if (index != -1) {
            val prescription = mockPrescriptions[index]

            if (current >= prescription.totalDoses) {

                val completedPrescription = prescription.copy(
                    status = PrescriptionStatus.COMPLETED
                )

                mockPrescriptions[index] = completedPrescription
            }
        }
    }

    fun getTakenCount(prescriptionId: Int): Int {
        return mockLogs.getOrDefault(prescriptionId, 0)
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

        private val mockPatients = mutableListOf(
            Patient(
                id = 1, //patientId
                userId = 102,
                doctorId = 1,
                firstName = "Иван",
                lastName = "Петров",
                birthDate = createDate(1985, 5, 15),
                gender = Gender.MALE,
                email = "test@gmail.com",
                allergies = listOf("Пенициллин"),
                chronicDiseases = listOf("Астма")
            ),
            Patient(
                id = 2, //patientId
                userId = 103,
                doctorId = 1,
                firstName = "Мария",
                lastName = "Сидорова",
                birthDate = createDate(1990, 8, 22),
                gender = Gender.FEMALE,
                email = "test2@gmail.com",
                allergies = emptyList(),
                chronicDiseases = listOf("Гипертония")
            ),
            Patient(
                id = 3, //patientId
                userId = 104,
                doctorId = 2,
                firstName = "Алексей",
                lastName = "Смирнов",
                birthDate = createDate(2000, 3, 10),
                gender = Gender.MALE,
                email = "test3@gmail.com",
                allergies = listOf("Арахис", "Лактоза"),
                chronicDiseases = emptyList()
            )
        )

        private val mockPrescriptions = mutableListOf(
            Prescription(
                id = 1,
                doctorId = 1,
                patientId = 1,
                medicine = mockMedicines[0],
                dosage = "400 мг",
                frequency = 3,
                durationDays = 7,
                startDate = formatDate(Date()),
                endDate = formatDate(addDays(Date(), 7)),
                status = PrescriptionStatus.ACTIVE,
                notes = "Принимать после еды"
            ),
            Prescription(
                id = 2,
                doctorId = 1,
                patientId = 2,
                medicine = mockMedicines[1],
                dosage = "500 мг",
                frequency = 2,
                durationDays = 10,
                startDate = formatDate(addDays(Date(), -15)),
                endDate = formatDate(addDays(Date(), -5)),
                status = PrescriptionStatus.COMPLETED,
                notes = ""
            ),
            Prescription(
                id = 3,
                doctorId = 2,
                patientId = 2,
                medicine = mockMedicines[3],
                dosage = "80 мг",
                frequency = 4,
                durationDays = 14,
                startDate = formatDate(addDays(Date(), -2)),
                endDate = formatDate(addDays(Date(), 5)),
                status = PrescriptionStatus.ACTIVE,
                notes = "Turip ip ip ip"
            )

        )

        private val mockLogs = mutableMapOf<Int, Int>()

        private val mockDoctors = listOf<Doctor>(
            Doctor(
                id = 1, //doctorId
                userId = 101,
                firstName = "Mikhail",
                lastName = "Zadornov",
                specialization = "Dentist",
                licenceNumber = "2312312321"
            ),
            Doctor(
                id = 2, //doctorId
                userId = 105,
                firstName = "Alexey",
                lastName = "Shevcov",
                specialization = "Cardiolog",
                licenceNumber = "26433443"
            )

        )


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