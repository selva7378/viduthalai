package com.selvaganesh7378.viduthalai.screens.lock

import android.content.ComponentName

// One-time side effects
sealed interface LockScreenEffect {
    object RequestNotificationPermission : LockScreenEffect
    data class RequestAdminPermission(val componentName: ComponentName, val explanation: String) :
        LockScreenEffect

    object NavigateToAppSettings : LockScreenEffect
    data class NavigateToTimerActivity(val hours: Int, val minutes: Int) : LockScreenEffect
    data class ShowToast(val message: String) : LockScreenEffect
}
