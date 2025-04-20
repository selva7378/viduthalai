package com.example.viduthalai.screens

import android.app.Activity
import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.core.content.ContextCompat
import com.example.viduthalai.receiver.MyDeviceAdminReceiver

@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    onAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val packageName = context.packageName

    // Permission states
    var hasOverlayPermission by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var hasUsageAccess by remember { mutableStateOf(checkUsageAccess(context)) }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    var hasAlarmPermission by remember { mutableStateOf(canScheduleExactAlarms(alarmManager!!)) }

    var hasAdminRights by remember { mutableStateOf(isDeviceAdminActive(context)) }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    // Launchers
    val overlayLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { hasOverlayPermission = Settings.canDrawOverlays(context) }

    val usageAccessLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { hasUsageAccess = checkUsageAccess(context) }

    val alarmPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { hasAlarmPermission = canScheduleExactAlarms(alarmManager!!) }

    val adminLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { hasAdminRights = isDeviceAdminActive(context) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasNotificationPermission = granted }

    val allPermissionsGranted = hasOverlayPermission && hasUsageAccess && hasAlarmPermission && hasAdminRights

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Required Permissions",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PermissionItem(
            name = "Display Over Other Apps",
            description = "Allows app to show content over other apps",
            isGranted = hasOverlayPermission,
            onToggle = {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                overlayLauncher.launch(intent)
            }
        )

        PermissionItem(
            name = "Usage Access",
            description = "Allows app to detect app usage",
            isGranted = hasUsageAccess,
            onToggle = {
                val intent = Intent(
                    Settings.ACTION_USAGE_ACCESS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                usageAccessLauncher.launch(intent)
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PermissionItem(
                name = "Exact Alarms",
                description = "Allows precise alarm scheduling",
                isGranted = hasAlarmPermission,
                onToggle = {
                    val intent = Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:$packageName")
                    )
                    alarmPermissionLauncher.launch(intent)
                }
            )
        }

        PermissionItem(
            name = "Device Administrator",
            description = "Required for security features",
            isGranted = hasAdminRights,
            onToggle = { handleAdminPermission(context, adminLauncher) }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionItem(
                name = "Notification",
                description = "Required to send notifications",
                isGranted = hasNotificationPermission,
                onToggle = {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onAllPermissionsGranted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = allPermissionsGranted
        ) {
            Text("Continue")
        }
    }
}


@Composable
fun PermissionItem(
    name: String,
    description: String,
    isGranted: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Switch(
            checked = isGranted,
            onCheckedChange = { onToggle() },
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

fun checkUsageAccess(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOp(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        ) == AppOpsManager.MODE_ALLOWED
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(
            "android:get_usage_stats",
            Process.myUid(),
            context.packageName
        ) == AppOpsManager.MODE_ALLOWED
    }
}

private fun handleAdminPermission(context: Context, launcher: ActivityResultLauncher<Intent>) {
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponent = ComponentName(context, MyDeviceAdminReceiver::class.java)

    if (!dpm.isAdminActive(adminComponent)) {
        launcher.launch(Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Required for security features")
        })
    } else {
        Toast.makeText(context, "Disable in device admin settings", Toast.LENGTH_SHORT).show()
    }
}


private fun isDeviceAdminActive(context: Context): Boolean {
    val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    return dpm.isAdminActive(ComponentName(context, MyDeviceAdminReceiver::class.java))
}

// Constants
private const val REQUEST_CODE_ALARM_PERMISSION = 1001
