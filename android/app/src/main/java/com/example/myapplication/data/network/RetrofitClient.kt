package com.example.myapplication.data.network

import com.example.myapplication.data.network.api.AuthApi
import com.example.myapplication.data.network.api.DoctorApi
import com.example.myapplication.presentation.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.31.10:8000/"
    //private const val BASE_URL = "http://10.0.2.2:8000/" //EMULATOR

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        val token = SessionManager.currentUser?.token

        if (token != null && token.isNotEmpty()) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val doctorApi: DoctorApi = retrofit.create(DoctorApi::class.java)
}