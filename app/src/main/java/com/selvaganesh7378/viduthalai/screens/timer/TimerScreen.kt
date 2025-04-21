package com.selvaganesh7378.viduthalai.screens.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.selvaganesh7378.viduthalai.datastore.DataStoreHelper
import com.selvaganesh7378.viduthalai.service.TimerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TimerScreen(
    hours: Int,
    minutes: Int,
    onTimerFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Log.e("timer activity", " hours = ${hours} and minutes = ${minutes}")
    val totalTimeMillis = (hours * 60L * 60L * 1000L) + (minutes * 60L * 1000L)
    var remainingTimeMillis by remember {

        mutableStateOf(totalTimeMillis)
    }
    val progress = remainingTimeMillis.toFloat() / totalTimeMillis
    val context = LocalContext.current

    // Create a reference that will be initialized later
    lateinit var serviceConnection: ServiceConnection

    serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            (service as? TimerService.LocalBinder)?.setUpdateCallback { newRemaining ->
                remainingTimeMillis = newRemaining

                if (newRemaining <= 0L) {
                    // Launch a coroutine to handle suspend functions
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            // 1. Handle timer finished callback
                            onTimerFinished()

                            // 2. Clear DataStore state (suspend function)
                            DataStoreHelper.setRestoreState(context, false)

                            // 3. Clean up service
                            context.unbindService(serviceConnection)
                            context.stopService(Intent(context, TimerService::class.java))
                        } catch (e: Exception) {
                            Log.e("TimerService", "Cleanup error", e)
                        }
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Handle unexpected service disconnection
            Log.w("TimerService", "Service unexpectedly disconnected")
        }
    }

    LaunchedEffect(Unit) {
        val isRestore = DataStoreHelper.isRestore(context)
        Log.e("timer screen", "${DataStoreHelper.isRestore(context)}")
        if (isRestore) {
            val (_, restoredRemaining) = DataStoreHelper.getTimerState(context)

            remainingTimeMillis = restoredRemaining
            if(remainingTimeMillis <= 0) {
                onTimerFinished()
            }
        }
        val serviceIntent = Intent(context, TimerService::class.java).apply {
            putExtra(TimerService.EXTRA_TOTAL_TIME, totalTimeMillis)
            putExtra(TimerService.EXTRA_REMAINING_TIME, remainingTimeMillis)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )
        Text(TimerService.formatTime(remainingTimeMillis))
    }
}


@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    // You can customize the preview with different time values
    TimerScreen(
        hours = 1,
        minutes = 30,
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true, name = "Short Timer")
@Composable
fun ShortTimerScreenPreview() {
    TimerScreen(
        hours = 0,
        minutes = 5,
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true, name = "Long Timer", backgroundColor = 0xFF000000)
@Composable
fun LongTimerScreenPreview() {
    TimerScreen(
        hours = 2,
        minutes = 45,
        modifier = Modifier.fillMaxSize()
    )
}