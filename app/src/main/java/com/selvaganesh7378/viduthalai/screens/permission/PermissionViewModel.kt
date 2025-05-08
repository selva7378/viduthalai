package com.selvaganesh7378.viduthalai.screens.permission

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
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.selvaganesh7378.viduthalai.receiver.MyDeviceAdminReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

//@HiltViewModel
class PermissionViewModel @Inject constructor() : ViewModel() {
    // Permission states
    var hasOverlayPermission by mutableStateOf(false)
    var hasUsageAccess by mutableStateOf(false)
    var hasAlarmPermission by mutableStateOf(false)
    var hasAdminRights by mutableStateOf(false)
    var hasNotificationPermission by mutableStateOf(false)

    fun checkAllPermissions(context: Context) {
        hasOverlayPermission = Settings.canDrawOverlays(context)
        hasUsageAccess = checkUsageAccess(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        hasAlarmPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager?.let { canScheduleExactAlarms(it) } ?: false
        } else {
            true
        }

        hasAdminRights = isDeviceAdminActive(context)

        hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    fun requestOverlayPermission(context: Context, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        launcher.launch(intent)
    }

    fun requestUsageAccessPermission(context: Context, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(
            Settings.ACTION_USAGE_ACCESS_SETTINGS,
            Uri.parse("package:${context.packageName}")
        )
        launcher.launch(intent)
    }

    fun requestAlarmPermission(context: Context, launcher: ActivityResultLauncher<Intent>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:${context.packageName}")
            )
            launcher.launch(intent)
        }
    }

    fun requestAdminPermission(context: Context, launcher: ActivityResultLauncher<Intent>) {
        handleAdminPermission(context, launcher)
    }

    fun requestNotificationPermission(launcher: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun checkUsageAccess(context: Context): Boolean {
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
        }
    }

    private fun isDeviceAdminActive(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return dpm.isAdminActive(ComponentName(context, MyDeviceAdminReceiver::class.java))
    }

    fun allPermissionsGranted(): Boolean {
        return hasOverlayPermission &&
                hasUsageAccess &&
                hasAlarmPermission &&
                hasAdminRights &&
                hasNotificationPermission
    }
}