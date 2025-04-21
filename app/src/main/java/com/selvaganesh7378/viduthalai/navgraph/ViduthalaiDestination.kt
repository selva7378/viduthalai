package com.selvaganesh7378.viduthalai.navgraph

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.selvaganesh7378.viduthalai.R

enum class ViduthalaiDestination(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    LockScreen(R.string.lock_screen, Icons.Default.Lock),
    ScheduleScreen(R.string.schedule_screen, Icons.Default.DateRange),
    SettingsScreen(R.string.settings_screen, Icons.Default.Settings),
}
