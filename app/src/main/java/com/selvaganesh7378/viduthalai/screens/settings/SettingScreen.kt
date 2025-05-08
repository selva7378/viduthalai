package com.selvaganesh7378.viduthalai.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.selvaganesh7378.viduthalai.screens.ScheduleScreen


@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
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
//@Composable
//fun SettingsScreen(modifier: Modifier = Modifier) {
//    val settingsOptions = listOf(
//        SettingItem(Icons.Default.Add, "Allowed Apps"),
//    )
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        Text(
//            text = "Settings",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//
//        settingsOptions.forEach { setting ->
//            SettingCard(setting)
//        }
//    }
//}
//
//@Composable
//fun SettingCard(setting: SettingItem) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { /* Handle click */ },
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Start
//        ) {
//            Icon(
//                imageVector = setting.icon,
//                contentDescription = setting.text,
//                modifier = Modifier.size(28.dp)
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Text(
//                text = setting.text,
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//    }
//}
//
//data class SettingItem(val icon: ImageVector, val text: String)
