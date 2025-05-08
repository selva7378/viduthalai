package com.selvaganesh7378.viduthalai.screens.lock

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.selvaganesh7378.viduthalai.TimerActivity
import com.selvaganesh7378.viduthalai.receiver.MyDeviceAdminReceiver
import kotlinx.coroutines.delay
import java.util.Calendar

// Define your allowed packages
private val ALLOWED_PACKAGES = arrayOf("com.example.viduthalai", "com.example.player")

@Composable
fun LockScreen(modifier: Modifier = Modifier) {
    var hours by rememberSaveable { mutableIntStateOf(0) }
    var minutes by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current
    val activity = context as Activity
    val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)
    var showSheet by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    val notificationPermission = android.Manifest.permission.POST_NOTIFICATIONS


    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Re-check device admin after permission
            if (devicePolicyManager.isAdminActive(componentName)) {
                showSheet = true
            } else {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                    putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "App requires admin access to lock the screen.")
                }
                context.startActivity(intent)
            }
        } else {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    DialExample(
        onConfirm = { hr, min ->
            hours = hr
            minutes = min
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    // Notification permission granted
                    ContextCompat.checkSelfPermission(context, notificationPermission) == PackageManager.PERMISSION_GRANTED -> {
                        // Device Admin granted?
                        if (devicePolicyManager.isAdminActive(componentName)) {
                            showSheet = true // Proceed
                        } else {
                            // Launch admin permission prompt
                            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "App requires admin access to lock the screen.")
                            }
                            context.startActivity(intent)
                        }
                    }

                    // Should show rationale for notifications
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, notificationPermission) -> {
                        Toast.makeText(
                            context,
                            "Please allow notification permission to continue.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {
                        // Launch permission request
                        notificationPermissionLauncher.launch(notificationPermission)
                    }
                }
            } else {
                showSheet = true
            }
        },
        onDismiss = { },
        modifier = modifier.fillMaxSize()
    )

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Permission Required") },
            text = { Text("This app needs notification permission to alert you when the timer completes. Please grant the permission in app settings.") },
            confirmButton = {
                Button(onClick = {
                    // Open app settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                    showPermissionRationale = false
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSheet) {
        ConfirmBottomSheet(
            onCancel = { showSheet = false },
            onTimeout = {
                showSheet = false
                Intent(context, TimerActivity::class.java).apply {
                    putExtra("hours", hours)
                    putExtra("minutes", minutes)
                    context.startActivity(this)
                }
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBottomSheet(
    onCancel: () -> Unit,
    onTimeout: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var progress by remember { mutableFloatStateOf(0f) }
    var timeLeft: Long by remember { mutableLongStateOf(5) }

    LaunchedEffect(Unit) {
        // Smooth progress update every 50ms
        val totalMillis = 5000L
        val frameDelay = 50L
        val totalSteps = (totalMillis / frameDelay).toInt()

        repeat(totalSteps) { step ->
            progress = step / totalSteps.toFloat()
            if (step % (1000 / frameDelay) == 0L) {
                timeLeft = 5 - (step / (1000 / frameDelay))
            }
            delay(frameDelay)
        }

        sheetState.hide()
        onTimeout()
    }

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
            LinearProgressIndicator(
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
