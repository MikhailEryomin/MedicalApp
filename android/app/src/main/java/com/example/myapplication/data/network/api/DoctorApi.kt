package com.example.myapplication.data.network.api

import com.example.myapplication.data.network.dto.CreatePatientRequestDto
import com.example.myapplication.data.network.dto.CreatePrescriptionRequestDto
import com.example.myapplication.data.network.dto.MedicineDto
import com.example.myapplication.data.network.dto.PatientDto
import com.example.myapplication.data.network.dto.PrescriptionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DoctorApi {
    @GET("api/doctors/me/patients")
    suspend fun getMyPatients(): List<PatientDto>

    @GET("api/patients/{id}")
    suspend fun getPatientById(@Path("id") id: Int): PatientDto

    @GET("api/patients/{id}/prescriptions")
    suspend fun getPatientPrescriptions(@Path("id") patientId: Int): List<PrescriptionDto>

    @GET("api/medicines")
    suspend fun searchMedicines(@Query("search") query: String): List<MedicineDto>

    @POST("api/prescriptions")
    suspend fun createPrescription(@Body request: CreatePrescriptionRequestDto): PrescriptionDto

    @GET("api/patients")
    suspend fun searchAllPatients(
        @Query("search") query: String,
        @Query("limit") limit: Int = 200
    ): List<PatientDto>

    @POST("api/doctors/me/patients/{patientId}")
    suspend fun attachPatient(@Path("patientId") patientId: Int)

    @POST("api/patients")
    suspend fun createPatient(@Body request: CreatePatientRequestDto): PatientDto
}