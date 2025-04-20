package com.example.viduthalai.receiver

import android.app.TaskStackBuilder
import com.example.viduthalai.datastore.DataStoreHelper


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.viduthalai.MainActivity
import com.example.viduthalai.TimerActivity
import com.example.viduthalai.service.TimerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                DataStoreHelper.setRestoreState(context, true)
            }
            // Retrieve timer state from DataStore
            val (totalTimeMillis, remainingTimeMillis) = DataStoreHelper.getTimerStateBlocking(context)

            // Check if there's an active timer
            if (totalTimeMillis > 0 && remainingTimeMillis > 0) {
                Log.d("BootReceiver", "Launching TimerActivity with timer: total=$totalTimeMillis, remaining=$remainingTimeMillis")

                // Convert milliseconds to hours and minutes
                val totalHours = (totalTimeMillis / (1000 * 60 * 60)).toInt()
                val totalMinutes = (totalTimeMillis / (1000 * 60) % 60).toInt()

                val remainingHours = (remainingTimeMillis / (1000 * 60 * 60)).toInt()
                val remainingMinutes = (remainingTimeMillis / (1000 * 60) % 60).toInt()

                val timerIntent = Intent(context, TimerActivity::class.java).apply {
                    // Pass both millis and human-readable values
                    putExtra("total_time_millis", totalTimeMillis)
                    putExtra("remaining_time_millis", remainingTimeMillis)
                    putExtra("hours", totalHours)
                    putExtra("minutes", totalMinutes)
                    putExtra("remaining_hours", remainingHours)
                    putExtra("remaining_minutes", remainingMinutes)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(timerIntent)
                    .startActivities()
            } else {
                Log.d("BootReceiver", "No active timer to restore")
            }
        }
    }
}