package com.example.myapplication.data

import android.util.Log
import com.example.myapplication.domain.User
import com.example.myapplication.domain.UserRole
import kotlinx.coroutines.delay

class AuthRepository {

    suspend fun login(email: String, password: String, role: UserRole): Result<User> {
        delay(500)

        val user = mockUsers.find { it.email == email && it.role == role }
        val correctPassword = mockPasswords[email]


        return (if (user != null && correctPassword == password) Result.success(user)
        else Result.failure(Exception("Неверный email или пароль"))) as Result<User>
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
            User(id = 102, email = "i-petrov@test.com", role = UserRole.PATIENT),
            User(id = 103, email = "m-sidorova@test.com", role = UserRole.PATIENT),
            User(id = 104, email = "a-smirnov@test.com", role = UserRole.PATIENT),
            User(id = 105, email = "doctor2@test.com", role = UserRole.DOCTOR)
        )

        private val mockPasswords = mutableMapOf(
            "doctor1@test.com" to "ddd1",
            "doctor2@test.com" to "ddd2",
            "i-petrov@test.com" to "iii",
            "m-sidorova@test.com" to "mmm",
            "a-smirnov@test.com" to "aaa"
        )
    }
}