package com.selvaganesh7378.viduthalai.screens.lock

// MODEL (State)
data class LockScreenState(
    val isConfirmationSheetVisible: Boolean = false,
    val isPermissionRationaleVisible: Boolean = false,
    val countdownProgress: Float = 0f,
    val countdownSeconds: Long = 5L,
    // Store the selected time to use it later
    val selectedHours: Int = 0,
    val selectedMinutes: Int = 0
)
