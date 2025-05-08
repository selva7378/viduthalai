package com.selvaganesh7378.viduthalai.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.selvaganesh7378.viduthalai.TimerActivity
import com.selvaganesh7378.viduthalai.datastore.DataStoreHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.app.TaskStackBuilder

class TimerRestoreWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                DataStoreHelper.setRestoreState(applicationContext, true)

                val totalTimeMillis = inputData.getLong("total_time_millis", 0)
                val remainingTimeMillis = inputData.getLong("remaining_time_millis", 0)

                if (totalTimeMillis > 0 && remainingTimeMillis > 0) {
                    Log.d("TimerRestoreWorker", "Restoring timer: total=$totalTimeMillis, remaining=$remainingTimeMillis")

                    val totalHours = (totalTimeMillis / (1000 * 60 * 60)).toInt()
                    val totalMinutes = (totalTimeMillis / (1000 * 60) % 60).toInt()
                    val remainingHours = (remainingTimeMillis / (1000 * 60 * 60)).toInt()
                    val remainingMinutes = (remainingTimeMillis / (1000 * 60) % 60).toInt()

                    val timerIntent = Intent(applicationContext, TimerActivity::class.java).apply {
                        putExtra("total_time_millis", totalTimeMillis)
                        putExtra("remaining_time_millis", remainingTimeMillis)
                        putExtra("hours", totalHours)
                        putExtra("minutes", totalMinutes)
                        putExtra("remaining_hours", remainingHours)
                        putExtra("remaining_minutes", remainingMinutes)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }

                    TaskStackBuilder.create(applicationContext)
                        .addNextIntentWithParentStack(timerIntent)
                        .startActivities()
                    Result.success()
                } else {
                    DataStoreHelper.setRestoreState(applicationContext, false)
                    Log.d("TimerRestoreWorker", "No active timer to restore")
                    Result.success()
                }
            } catch (e: Exception) {
                Log.e("TimerRestoreWorker", "Error restoring timer", e)
                Result.failure()
            }
        }
    }

    companion object {
        fun schedule(context: Context, totalTimeMillis: Long, remainingTimeMillis: Long) {
            val inputData = Data.Builder()
                .putLong("total_time_millis", totalTimeMillis)
                .putLong("remaining_time_millis", remainingTimeMillis)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<TimerRestoreWorker>()
                .setInputData(inputData)
//                .setInitialDelay(1, java.util.concurrent.TimeUnit.MINUTES) // Delay after boot
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}