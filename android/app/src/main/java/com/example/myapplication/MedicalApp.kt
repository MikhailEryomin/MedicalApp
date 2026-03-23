package com.example.myapplication

import android.app.Application
import com.example.myapplication.presentation.SessionManager

class MedicalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализируем SessionManager при старте приложения
        SessionManager.init(this)
    }
}