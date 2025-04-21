package com.selvaganesh7378.viduthalai

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.selvaganesh7378.viduthalai.datastore.DataStoreHelper
import com.selvaganesh7378.viduthalai.receiver.MyDeviceAdminReceiver
import com.selvaganesh7378.viduthalai.screens.timer.TimerScreen
import com.selvaganesh7378.viduthalai.ui.theme.ViduthalaiTheme
import kotlinx.coroutines.launch

class TimerActivity : ComponentActivity() {
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName
    private var isTimerFinished by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize device policy manager
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)





        setContent {
            ViduthalaiTheme {
                val hours = intent.getIntExtra("hours", 0)
                val minutes = intent.getIntExtra("minutes", 0)
                if(hours <= 0 && minutes <= 0) {

                    isTimerFinished = true;
                }

                TimerScreen(
                    hours = hours,
                    minutes = minutes,
                    onTimerFinished = {
                        isTimerFinished = true
                    }
                )
            }
        }

        // Add back pressed callback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTimerFinished) {
                    finish() // Allow back press when timer done
                } else if (devicePolicyManager.isAdminActive(adminComponent)) {
                    devicePolicyManager.lockNow()
                }
            }
        })

        // Hide system UI for full immersion
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.insetsController?.let {
            it.hide(WindowInsets.Type.systemBars())
//            it.systemBarsBehavir = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        };
    }
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (!isTimerFinished && ::devicePolicyManager.isInitialized && devicePolicyManager.isAdminActive(adminComponent)) {
            devicePolicyManager.lockNow()
            val intent = Intent(this, TimerActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
            startActivity(intent)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus && !isTimerFinished &&
            ::devicePolicyManager.isInitialized &&
            devicePolicyManager.isAdminActive(adminComponent)) {

//            devicePolicyManager.lockNow()
            val intent = Intent(this, TimerActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isTimerFinished) {
            lifecycleScope.launch {
                val (_, remainingTime) = DataStoreHelper.getTimerState(this@TimerActivity)
                if (remainingTime > 0) {
                    DataStoreHelper.setRestoreState(this@TimerActivity, true)
                }else{
                    DataStoreHelper.setRestoreState(this@TimerActivity, false)
                }
                Log.e("timer activity", "${DataStoreHelper.isRestore(this@TimerActivity)}")
            }
        }
    }

}

@Composable
fun demo() {
    Text(
        text = "Timer Screen",
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
    )
}