package com.example.myapplication.presentation.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.myapplication.domain.Prescription
import java.util.Calendar

class ReminderManager(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleRemindersForPrescription(prescription: Prescription) {

        // Допустим, frequency = 2 (два раза в день).
        // В реальном приложении пользователь сам выбирает время (09:00, 21:00).
        // Для простоты сейчас мы просто ставим будильник на "через 1 минуту" для демонстрации,
        // либо раскидываем время жестко (например, 09:00, 14:00, 20:00 в зависимости от frequency).

        val timesToRing = calculateTimesForFrequency(prescription.frequency)

        for ((index, timeCal) in timesToRing.withIndex()) {

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(ReminderReceiver.EXTRA_MEDICINE_NAME, prescription.medicine.name)
                putExtra(ReminderReceiver.EXTRA_DOSAGE, prescription.dosage)
                putExtra(ReminderReceiver.EXTRA_PRESCRIPTION_ID, prescription.id)
            }

            // Создаем уникальный ID для каждого будильника (например: 1001, 1002, где 1 - id рецепта)
            val requestCode = prescription.id * 1000 + index

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.e("ReminderManager", "Нет разрешения на точные будильники!")
                    return // В реальном приложении тут нужно кинуть юзера в настройки
                }
            }

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeCal.timeInMillis,
                pendingIntent
            )

            Log.d("ReminderManager", "Будильник установлен на: ${timeCal.time}")
        }
    }

    fun cancelRemindersForPrescription(prescriptionId: Int, frequency: Int) {
        for (i in 0 until frequency) {
            val intent = Intent(context, ReminderReceiver::class.java)
            val requestCode = prescriptionId * 1000 + i
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun calculateTimesForFrequency(frequency: Int): List<Calendar> {
        val times = mutableListOf<Calendar>()
        val now = Calendar.getInstance()

        val testCal = Calendar.getInstance()
        testCal.add(Calendar.SECOND, 10)
        times.add(testCal)

        return times
    }
}