package com.example.viduthalai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.viduthalai.datastore.DataStoreHelper
import com.example.viduthalai.navgraph.ViduthalaiDestination
import com.example.viduthalai.ui.theme.ViduthalaiTheme
import com.example.viduthalai.navgraph.ViduthalaiNavigationWrapperUI
import com.example.viduthalai.screens.PermissionScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ViduthalaiTheme {
                Viduthalai()
            }
        }
    }
}

@Composable
fun Viduthalai(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Observe first launch state using your existing DataStoreHelper
    val isFirstLaunch by produceState<Boolean?>(initialValue = null) {
        value = DataStoreHelper.isFirstLaunch(context)
    }

    // Handle loading/ready states
    when (isFirstLaunch) {
        true -> PermissionScreen(
            onAllPermissionsGranted = {
                scope.launch {
                    DataStoreHelper.setFirstLaunchCompleted(context)
                }
            }
        )
        false -> ViduthalaiNavigationWrapperUI()
        null -> FullScreenLoader() // Optional loading state
    }
}

// Optional loading component
@Composable
fun FullScreenLoader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ViduthalaiTheme {
        Viduthalai()
    }
}