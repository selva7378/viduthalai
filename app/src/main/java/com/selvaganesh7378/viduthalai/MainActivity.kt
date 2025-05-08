package com.selvaganesh7378.viduthalai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.selvaganesh7378.viduthalai.datastore.DataStoreHelper
import com.selvaganesh7378.viduthalai.model.onboardingPages
import com.selvaganesh7378.viduthalai.ui.theme.ViduthalaiTheme
import com.selvaganesh7378.viduthalai.navgraph.ViduthalaiNavigationWrapperUI
import com.selvaganesh7378.viduthalai.screens.onboarding.OnBoardingScreen
import com.selvaganesh7378.viduthalai.screens.permission.PermissionScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
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


    // Track first launch state
    var isFirstLaunch by remember { mutableStateOf<Boolean?>(null) }

    // Observe first launch state asynchronously
    LaunchedEffect(Unit) {
        isFirstLaunch = DataStoreHelper.isFirstLaunch(context)
    }

    when (isFirstLaunch) {
//        true -> PermissionScreen(
//            onAllPermissionsGranted = {
//                scope.launch {
//                    DataStoreHelper.setFirstLaunchCompleted(context)
//                    isFirstLaunch = false // Update state to navigate to main screen
//                }
//            }
//        )
        true -> OnBoardingScreen(
            onboardingPages,
            onAgree = {
                scope.launch {
                    DataStoreHelper.setFirstLaunchCompleted(context)
                    isFirstLaunch = false // Update state to navigate to main screen
                }
            }
        )
        false -> ViduthalaiNavigationWrapperUI()
        null -> FullScreenLoader() // Show a loading state while checking first launch
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