package com.example.viduthalai.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.viduthalai.R
import com.example.viduthalai.datastore.DataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerService : Service() {
    private var totalTimeMillis: Long = 0
    private var remainingTimeMillis: Long = 0
    private var updateCallback: ((Long) -> Unit)? = null
    private val channelId = "timer_channel"
    private lateinit var notificationManager: NotificationManager

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun setUpdateCallback(callback: (Long) -> Unit) {
            updateCallback = callback
        }
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val isRestore = intent?.getBooleanExtra("RESTORE_TIMER", false) ?: false

        CoroutineScope(Dispatchers.IO).launch {

            totalTimeMillis = intent?.getLongExtra(EXTRA_TOTAL_TIME, 0L) ?: 0L

            remainingTimeMillis = intent?.getLongExtra(EXTRA_REMAINING_TIME, 0L) ?: 0L

            startForeground(NOTIFICATION_ID, createNotification(remainingTimeMillis))
            startCountdownUpdates()
        }

        return START_STICKY
    }

    private fun startCountdownUpdates() {
        CoroutineScope(Dispatchers.Default).launch {
            while (remainingTimeMillis > 0) {
                delay(1000)
                remainingTimeMillis -= 1000
                updateCallback?.invoke(remainingTimeMillis)
                updateNotification(remainingTimeMillis)

                // Save the current timer state
                DataStoreHelper.saveTimerState(
                    context = this@TimerService,
                    totalTime = totalTimeMillis,
                    remainingTime = remainingTimeMillis
                )
            }

            if(remainingTimeMillis <= 0) {
                DataStoreHelper.clearTimerState(this@TimerService)
            }


            stopSelf()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Timer Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timer is running"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC // 🔑 Lock screen enabled
                setShowBadge(false) // Optional: Hide badge icon
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(remainingMillis: Long): Notification {
        val progress = if (totalTimeMillis > 0) {
            100 - ((remainingMillis.toFloat() / totalTimeMillis) * 100).toInt()
        } else {
            100
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Focus Timer Active")
            .setContentText("Time remaining: ${formatTime(remainingMillis)}")
            .setSmallIcon(R.drawable.running_icon)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // 🔑 Lock screen visibility
            .setCategory(Notification.CATEGORY_PROGRESS) // Helps system classify it
            .build()
    }

    private fun updateNotification(remainingMillis: Long) {
        notificationManager.notify(NOTIFICATION_ID, createNotification(remainingMillis))
    }

    companion object {
        const val NOTIFICATION_ID = 101
        const val EXTRA_TOTAL_TIME = "total_time"
        const val EXTRA_REMAINING_TIME = "extra_remaining_time"

        fun formatTime(timeMillis: Long): String {
            val totalSeconds = timeMillis / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }
}