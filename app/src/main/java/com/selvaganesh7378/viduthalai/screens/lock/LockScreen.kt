package com.selvaganesh7378.viduthalai.screens.lock

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.selvaganesh7378.viduthalai.TimerActivity
import java.util.Calendar

// Define your allowed packages
private val ALLOWED_PACKAGES = arrayOf("com.example.viduthalai", "com.example.player")

@Composable
fun LockScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    DialExample(
        onConfirm = { hours, minutes ->
            // Launch TimerActivity as a separate activity
            Intent(context, TimerActivity::class.java).apply {
                putExtra("hours", hours)
                putExtra("minutes", minutes)
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(this)
            }
        },
        onDismiss = { /* Handle dismiss if needed */ },
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialExample(
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    val isButtonEnabled = timePickerState.hour != 0 || timePickerState.minute != 0

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TimePicker(state = timePickerState)

        Button(
            onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            },
            enabled = isButtonEnabled
        ) {
            Text("Lock Now")
        }
    }
}

