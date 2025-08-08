package com.selvaganesh7378.viduthalai.screens.lock

import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.selvaganesh7378.viduthalai.TimerActivity

@Composable
fun LockScreen(
    modifier: Modifier = Modifier,
    viewModel: LockScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onEvent(LockScreenEvent.NotificationPermissionResult(isGranted))
    }

    LaunchedEffect(key1 = viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LockScreenEffect.NavigateToTimerActivity -> {
                    Intent(context, TimerActivity::class.java).apply {
                        putExtra("hours", effect.hours)
                        putExtra("minutes", effect.minutes)
                        context.startActivity(this)
                    }
                }
                is LockScreenEffect.RequestAdminPermission -> {
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, effect.componentName)
                        putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, effect.explanation)
                    }
                    context.startActivity(intent)
                }
                is LockScreenEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                LockScreenEffect.RequestNotificationPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                LockScreenEffect.NavigateToAppSettings -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    DialExample(
        onConfirm = { hr, min ->
            viewModel.onEvent(LockScreenEvent.TimeSelected(hr, min))
        },
        modifier = modifier.fillMaxSize()
    )

    if (state.isPermissionRationaleVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(LockScreenEvent.RationaleDialogDismissed) },
            title = { Text("Permission Required") },
            text = { Text("This app needs notification permission to alert you when the timer completes. Please grant the permission in app settings.") },
            confirmButton = {
                Button(onClick = { viewModel.onEvent(LockScreenEvent.OpenAppSettings) }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(LockScreenEvent.RationaleDialogDismissed) }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (state.isConfirmationSheetVisible) {
        ConfirmBottomSheet(
            progress = state.countdownProgress,
            timeLeft = state.countdownSeconds,
            onCancel = { viewModel.onEvent(LockScreenEvent.CancelConfirmation) }
        )
    }
}

@Preview
@Composable
fun LockScreenPreview() {
    LockScreen()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialExample(
    onConfirm: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 0,
        is24Hour = true,
    )

    val isButtonEnabled = timePickerState.hour != 0 || timePickerState.minute != 0

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TimePicker(state = timePickerState)

        val hours = timePickerState.hour
        val minutes = timePickerState.minute

        val buttonText = buildString {
            append("Lock ")
            if (hours > 0) append("$hours hr${if (hours > 1) "s" else ""}")
            if (hours > 0 && minutes > 0) append(" and ")
            if (minutes > 0) append("$minutes min${if (minutes > 1) "s" else ""}")
        }

        Button(
            onClick = { onConfirm(hours, minutes) },
            enabled = isButtonEnabled
        ) {
            Text(buttonText)
        }
    }
}

@Preview
@Composable
fun DialExamplePreview() {
    DialExample(onConfirm = { _, _ -> })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConfirmBottomSheet(
    progress: Float,
    timeLeft: Long,
    onCancel: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onCancel() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Locking in $timeLeft second${if (timeLeft != 1L) "s" else ""}...", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            LinearWavyProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { onCancel() }) {
                Text("Cancel")
            }
        }
    }
}

@Preview
@Composable
fun ConfirmBottomSheetPreview() {
    ConfirmBottomSheet(progress = 0.5f, timeLeft = 3, onCancel = {})
}
