package com.example.myapplication.presentation.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import com.example.myapplication.presentation.MainActivity

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME) ?: "Лекарство"
        val dosage = intent.getStringExtra(EXTRA_DOSAGE) ?: ""
        val prescriptionId = intent.getIntExtra(EXTRA_PRESCRIPTION_ID, 0)

        showNotification(context, medicineName, dosage, prescriptionId)
    }

    private fun showNotification(context: Context, medicineName: String, dosage: String, prescriptionId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Напоминания о лекарствах",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о времени приема лекарств"
            }
            notificationManager.createNotificationChannel(channel)
        }
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            prescriptionId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle("Время приема лекарства!")
            .setContentText("Пора принять: $medicineName $dosage")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Уведомление исчезнет после клика
            .build()


        notificationManager.notify(prescriptionId, notification)
    }

    companion object {
        const val CHANNEL_ID = "MEDICATION_REMINDERS_CHANNEL"
        const val EXTRA_MEDICINE_NAME = "medicine_name"
        const val EXTRA_DOSAGE = "dosage"
        const val EXTRA_PRESCRIPTION_ID = "prescription_id"
    }
}