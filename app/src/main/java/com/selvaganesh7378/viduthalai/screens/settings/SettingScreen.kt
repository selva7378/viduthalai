package com.selvaganesh7378.viduthalai.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.selvaganesh7378.viduthalai.screens.schedule.ScheduleScreen


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ContainedLoadingIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Under Development")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ScheduleScreen()
}

