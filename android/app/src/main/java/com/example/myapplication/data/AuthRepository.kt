package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.data.network.RetrofitClient
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

    suspend fun register(email: String, role: UserRole): User {
        delay(500)
        // TODO: Когда будет API - POST /auth/register
        val newId = (mockUsers.maxOfOrNull { it.id } ?: 0).plus(1)
        val newUser = User(id = newId, email = email, role = role)
        mockUsers.add(newUser)
        mockPasswords[newUser.email] = HARDCODED_STANDARD_PASSWORD
        Log.d("TAG", mockUsers.toString())
        Log.d("TAG","Success new user added!")
        return newUser
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