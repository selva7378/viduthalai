package com.selvaganesh7378.viduthalai.screens.lock

// INTENT (Event)
sealed interface LockScreenEvent {
    // User clicks the main "Lock" button
    data class TimeSelected(val hours: Int, val minutes: Int) : LockScreenEvent

    // User clicks "Cancel" in the confirmation sheet
    object CancelConfirmation : LockScreenEvent

    // Fired from the ViewModel's coroutine when the countdown finishes
    object CountdownFinished : LockScreenEvent

    // Fired when the result from the notification permission request is received
    data class NotificationPermissionResult(val isGranted: Boolean) : LockScreenEvent

    // User dismisses the rationale dialog
    object RationaleDialogDismissed : LockScreenEvent

    // User clicks "Open Settings" in the rationale dialog
    object OpenAppSettings : LockScreenEvent

    // Fired when the screen is resumed (to re-check device admin)
    object OnResume : LockScreenEvent
}
