package com.selvaganesh7378.viduthalai.navgraph

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.selvaganesh7378.viduthalai.screens.lock.LockScreen
import com.selvaganesh7378.viduthalai.screens.ScheduleScreen
import com.selvaganesh7378.viduthalai.screens.settings.SettingsScreen

@Composable
fun ViduthalaiNavigationWrapperUI(modifier: Modifier = Modifier) {
    var selectedDestination: ViduthalaiDestination by rememberSaveable {
        mutableStateOf(ViduthalaiDestination.LockScreen)
    }

    val windowSize = with(LocalDensity.current) {
        currentWindowSize().toSize().toDpSize()
    }
    val layoutType = if (windowSize.width >= 1200.dp) {
        NavigationSuiteType.NavigationDrawer
    } else if (windowSize.height < 480.dp) {
        NavigationSuiteType.NavigationRail
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
            currentWindowAdaptiveInfo()
        )
    }

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = {
            ViduthalaiDestination.entries.forEach {
                item(
                    selected = it == selectedDestination,
                    onClick = { selectedDestination = it },
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(it.labelRes)
                        )
                    },
                    label = {
                        Text(text = stringResource(it.labelRes))
                    },
                )
            }
        }
    ) {
        when (selectedDestination) {
            ViduthalaiDestination.LockScreen -> LockScreen()
            ViduthalaiDestination.ScheduleScreen -> ScheduleScreen()
            ViduthalaiDestination.SettingsScreen -> SettingsScreen()
        }
    }
}
