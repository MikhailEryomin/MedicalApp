package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.domain.User
import com.example.myapplication.domain.UserRole

object SessionManager {

    private const val PREFS_NAME = "medical_app_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_ROLE = "user_role"

    private lateinit var prefs: SharedPreferences

    var currentUser: User? = null
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        restoreUser()
    }

    fun saveUser(user: User) {
        currentUser = user
        prefs.edit().apply {
            putString(KEY_TOKEN, user.token)
            putInt(KEY_USER_ID, user.id)
            putString(KEY_EMAIL, user.email)
            putString(KEY_ROLE, user.role.name)
            apply()
        }
    }

    fun clear() {
        currentUser = null
        prefs.edit().clear().apply()
    }

    // Внутренняя функция для восстановления при старте приложения
    private fun restoreUser() {
        val token = prefs.getString(KEY_TOKEN, null)
        val roleStr = prefs.getString(KEY_ROLE, null)

        if (!token.isNullOrEmpty() && !roleStr.isNullOrEmpty()) {
            val role = try {
                UserRole.valueOf(roleStr)
            } catch (e: Exception) {
                UserRole.PATIENT
            }

            currentUser = User(
                id = prefs.getInt(KEY_USER_ID, 0),
                email = prefs.getString(KEY_EMAIL, "") ?: "",
                role = role,
                token = token,
            )
        }
    }
}