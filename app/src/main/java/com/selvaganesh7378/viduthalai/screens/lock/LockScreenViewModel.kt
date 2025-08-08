package com.selvaganesh7378.viduthalai.screens.lock

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.selvaganesh7378.viduthalai.receiver.MyDeviceAdminReceiver
import com.selvaganesh7378.viduthalai.screens.lock.LockScreenEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(LockScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LockScreenEffect>()
    val effect = _effect.asSharedFlow()

    private val devicePolicyManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)
    private val notificationPermission = android.Manifest.permission.POST_NOTIFICATIONS

    private var countdownJob: Job? = null

    fun onEvent(event: LockScreenEvent) {
        when (event) {
            is LockScreenEvent.TimeSelected -> {
                _state.update {
                    it.copy(
                        selectedHours = event.hours,
                        selectedMinutes = event.minutes
                    )
                }
                checkPermissionsAndProceed()
            }

            is LockScreenEvent.NotificationPermissionResult -> {
                if (event.isGranted) {
                    // Re-check device admin after permission
                    if (devicePolicyManager.isAdminActive(componentName)) {
                        startCountdown()
                    } else {
                        requestAdminPermission()
                    }
                } else {
                    viewModelScope.launch {
                        _effect.emit(ShowToast("Notification permission denied"))
                    }
                }
            }

            LockScreenEvent.OnResume -> {
                // When the user comes back from the admin screen, check again and start countdown
                if (devicePolicyManager.isAdminActive(componentName)) {
                   startCountdown()
                }
            }

            LockScreenEvent.CancelConfirmation -> {
                countdownJob?.cancel()
                countdownJob = null
                _state.update { it.copy(isConfirmationSheetVisible = false) }
            }

            LockScreenEvent.CountdownFinished -> {
                _state.update { it.copy(isConfirmationSheetVisible = false) }
                viewModelScope.launch {
                    _effect.emit(
                        NavigateToTimerActivity(
                            hours = _state.value.selectedHours,
                            minutes = _state.value.selectedMinutes
                        )
                    )
                }
                countdownJob = null
            }

            LockScreenEvent.OpenAppSettings -> {
                viewModelScope.launch { _effect.emit(LockScreenEffect.NavigateToAppSettings) }
                _state.update { it.copy(isPermissionRationaleVisible = false) }
            }

            LockScreenEvent.RationaleDialogDismissed -> {
                _state.update { it.copy(isPermissionRationaleVisible = false) }
            }
        }
    }


    private fun checkPermissionsAndProceed() {
        when {
            // Notification permission granted
            ContextCompat.checkSelfPermission(
                context,
                notificationPermission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Device Admin granted?
                if (devicePolicyManager.isAdminActive(componentName)) {
                    startCountdown() // Proceed
                } else {
                    // Launch admin permission prompt
                    requestAdminPermission()
                }
            }

            else -> {
                // Launch permission request
                viewModelScope.launch {
                    _effect.emit(LockScreenEffect.RequestNotificationPermission)
                }
            }
        }
    }

    private fun requestAdminPermission() {
        viewModelScope.launch {
            _effect.emit(
                LockScreenEffect.RequestAdminPermission(
                    componentName = componentName,
                    explanation = "App requires admin access to lock the screen."
                )
            )
        }
    }

    private fun startCountdown() {
        // Cancel any existing job to avoid multiple countdowns running
        countdownJob?.cancel()
        _state.update { it.copy(isConfirmationSheetVisible = true) }
        countdownJob = viewModelScope.launch {
            val totalMillis = 5000L
            val frameDelay = 50L
            val totalSteps = (totalMillis / frameDelay).toInt()

            repeat(totalSteps) { step ->
                val progress = step / totalSteps.toFloat()
                val timeLeft = 5 - ((step * frameDelay) / 1000L)
                _state.update {
                    it.copy(
                        countdownProgress = progress,
                        countdownSeconds = timeLeft
                    )
                }
                delay(frameDelay)
            }
            onEvent(LockScreenEvent.CountdownFinished)
        }
    }
}
