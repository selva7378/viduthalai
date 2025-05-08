package com.selvaganesh7378.viduthalai.screens.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PermissionScreen(
    modifier: Modifier = Modifier,
    onAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PermissionViewModel = hiltViewModel()

    // Check permissions when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.checkAllPermissions(context)
    }

    // Launchers
    val overlayLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.checkAllPermissions(context) }

    val usageAccessLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.checkAllPermissions(context) }

    val alarmPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.checkAllPermissions(context) }

    val adminLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { viewModel.checkAllPermissions(context) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.hasNotificationPermission = granted
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()  // Add status bar padding
            .navigationBarsPadding()  // Add navigation bar padding
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Required Permissions",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PermissionItem(
            name = "Grant overlay permission",
            description = "Allows app to show content over other apps",
            isGranted = viewModel.hasOverlayPermission,
            onToggle = { viewModel.requestOverlayPermission(context, overlayLauncher) }
        )

        PermissionItem(
            name = "Usage Access",
            description = "Allows app to detect app usage",
            isGranted = viewModel.hasUsageAccess,
            onToggle = { viewModel.requestUsageAccessPermission(context, usageAccessLauncher) }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PermissionItem(
                name = "Grant Exact Alarms Permission",
                description = "Allows precise alarm scheduling",
                isGranted = viewModel.hasAlarmPermission,
                onToggle = { viewModel.requestAlarmPermission(context, alarmPermissionLauncher) }
            )
        }

        PermissionItem(
            name = "Grant Device Administrator",
            description = "Required for security features",
            isGranted = viewModel.hasAdminRights,
            onToggle = { viewModel.requestAdminPermission(context, adminLauncher) }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionItem(
                name = "Notification",
                description = "Required to send notifications",
                isGranted = viewModel.hasNotificationPermission,
                onToggle = { viewModel.requestNotificationPermission(notificationPermissionLauncher) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onAllPermissionsGranted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = viewModel.allPermissionsGranted()
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
}


@Preview
@Composable
fun PermissionScreenPreview() {
    PermissionScreen(onAllPermissionsGranted = {})

}