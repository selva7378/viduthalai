package com.example.viduthalai.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TimerScreen(hours: Int, minutes: Int, modifier: Modifier = Modifier) {
    CircularCountdownTimer(hours = hours, minutes = minutes)
}

@Composable
fun CircularCountdownTimer(hours: Int, minutes: Int) {
    val context = LocalContext.current
    val totalTimeMillis = (hours * 60L * 60L * 1000L) + (minutes * 60L * 1000L)
    var remainingTimeMillis by remember { mutableStateOf(totalTimeMillis) }
    val progress = remainingTimeMillis.toFloat() / totalTimeMillis
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            while (remainingTimeMillis > 0) {
                delay(1000)
                remainingTimeMillis -= 1000
            }
            // Exit lock task when timer ends
            (context as Activity).stopLockTask()
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(150.dp),
            strokeWidth = 8.dp
        )

        Text(
            text = formatTime(remainingTimeMillis),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

// Function to format time as HH:MM:SS
fun formatTime(timeMillis: Long): String {
    val totalSeconds = timeMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
