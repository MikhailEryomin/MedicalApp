package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.data.network.RetrofitClient
import com.example.myapplication.data.network.dto.RegisterPatientDto
import com.example.myapplication.data.network.dto.toDomain
import com.example.myapplication.domain.User
import com.example.myapplication.domain.UserRole
import kotlinx.coroutines.delay

class AuthRepository {

    suspend fun login(email: String, password: String, role: UserRole): Result<User> {
        return try {

            val requestBody = mapOf(
                "email" to email,
                "password" to password,
                "role" to role.name
            )

            val responseDto = RetrofitClient.authApi.login(requestBody)
            val user = responseDto.user.toDomain(email, responseDto.token)

            Log.d("AuthRepository", "Успешный логин! Токен получен.")
            Result.success(user)

        } catch (e: Exception) {
            Log.e("AuthRepository", "Ошибка логина: ${e.message}")
            Result.failure(Exception("Неверный email или пароль. Либо сервер недоступен."))
        }
    }

    suspend fun registerPatient(
        firstName: String,
        lastName: String,
        birthDate: String,
        gender: String,
        email: String,
        password: String,
        allergies: List<String>,
        diseases: List<String>
    ): Result<User> {
        return try {

            val birthDateForServer = try {
                val inputFormat = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
                val outputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val date = inputFormat.parse(birthDate)
                if (date != null) outputFormat.format(date) else ""
            } catch (e: Exception) { "" }

            // 2. Собираем DTO
            val requestBody = RegisterPatientDto(
                firstName = firstName,
                lastName = lastName,
                birthDate = birthDateForServer,
                gender = gender,
                email = email,
                password = password,
                allergies = allergies,
                diseases = diseases
            )

            val responseDto = RetrofitClient.authApi.registerPatient(requestBody)

            val user = User(
                id = responseDto.user.id,
                email = email,
                role = UserRole.PATIENT,
                token = responseDto.token
            )

            Log.d("AuthRepository", "Успешная регистрация пациента!")
            Result.success(user)

        } catch (e: Exception) {
            Log.e("AuthRepository", "Ошибка регистрации: ${e.message}")
            Result.failure(Exception("Registration failed. Email might be already in use."))
        }
    }

    fun logout() {
        // TODO: Очистить токен из DataStore
    }

    companion object {
        private const val HARDCODED_STANDARD_PASSWORD = "1234"

        private val mockUsers = mutableListOf(
            User(id = 101, email = "doctor1@test.com", role = UserRole.DOCTOR),
            User(id = 102, email = "i-petrov@test.com", role = UserRole.PATIENT)
        )
        private val mockPasswords = mutableMapOf(
            "doctor1@test.com" to "ddd1",
            "i-petrov@test.com" to "iii"
        )
    }
}