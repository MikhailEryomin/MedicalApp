package com.example.myapplication.data.network.api

import retrofit2.http.POST
import retrofit2.http.Path

interface PatientApi {

    @POST("api/prescriptions/{id}/log")
    suspend fun markAsTaken(@Path("id") prescriptionId: Int)

}