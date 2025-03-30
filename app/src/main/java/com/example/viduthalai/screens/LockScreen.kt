package com.example.viduthalai.screens

import android.app.Activity
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

// Define your allowed packages
private val ALLOWED_PACKAGES = arrayOf("com.example.viduthalai", "com.example.player")

@Composable
fun LockScreen(modifier: Modifier = Modifier) {
    var showTimerScreen by remember { mutableStateOf(false) }
    var selectedHours by remember { mutableStateOf(0) }
    var selectedMinutes by remember { mutableStateOf(0) }

    if (showTimerScreen) {
        TimerScreen(
            hours = selectedHours,
            minutes = selectedMinutes,
            modifier = modifier
        )
    } else {
        DialExample(
            onConfirm = { hours, minutes ->
                selectedHours = hours
                selectedMinutes = minutes
                showTimerScreen = true
            },
            onDismiss = { /* Handle dismiss if needed */ },
            modifier = modifier.fillMaxSize()
        )
    }
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

