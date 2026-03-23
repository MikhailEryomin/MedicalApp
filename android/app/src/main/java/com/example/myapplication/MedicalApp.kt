package com.example.myapplication

import android.app.Application
import com.example.myapplication.SessionManager

class MedicalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализируем SessionManager при старте приложения
        SessionManager.init(this)
    }
}