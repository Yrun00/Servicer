package com.github.servicer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class SecondService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SecondService", "SecondService started")

        // 1️⃣ Создаём Notification Channel (нужно на Android 8+)
        createNotificationChannel()

        // 2️⃣ Создаём Notification
        val notification = NotificationCompat.Builder(this, "service_channel")
            .setContentTitle("Service running...")
            .setContentText("Waiting 40 seconds...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        // 3️⃣ Запускаем как Foreground Service
        startForeground(1, notification)

        // 4️⃣ Фоновый поток на 40 сек
        serviceScope.launch {
            delay(40000)  // Ждём 40 сек
            Log.d("SecondService", "40 seconds passed, opening activity")

            // Открываем Activity из FirstService приложения
            withContext(Dispatchers.Main) {
                val intent = Intent()
                intent.setClassName(
                    "com.github.boundservicemultiprocess",  // package первого приложения
                    "com.github.boundservicemultiprocess.MainActivity"  // activity
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            stopSelf()  // Останавливаем сервис
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "service_channel",
            "Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d("SecondService", "SecondService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
