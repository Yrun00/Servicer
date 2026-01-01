package com.github.servicer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class FirstService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("FirstService", "FirstService started")

        serviceScope.launch {
            delay(5000)
            Log.d("FirstService", "5 seconds passed, starting SecondService")

            val secondServiceIntent = Intent(this@FirstService, SecondService::class.java)
            startService(secondServiceIntent)
        }
        return START_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d("FirstService", "FirstService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}