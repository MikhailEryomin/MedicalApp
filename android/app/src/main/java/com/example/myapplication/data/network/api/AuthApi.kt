package com.example.myapplication.data.network.api

import com.example.myapplication.data.network.dto.AuthResponseDto
import com.example.myapplication.data.network.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body body: Map<String, String>): AuthResponseDto

    @GET("api/users/me")
    suspend fun getUserInfo(): UserDto

    // POST /auth/register добавим позже, когда дойдем до создания пациента
}