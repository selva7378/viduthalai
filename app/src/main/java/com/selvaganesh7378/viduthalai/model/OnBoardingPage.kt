package com.selvaganesh7378.viduthalai.model

import androidx.annotation.DrawableRes
import com.selvaganesh7378.viduthalai.R

// Data class definition
data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Welcome to FocusLock",
        description = "Experience a digital detox by locking your device and reclaiming your time.",
        imageRes = R.drawable.demo_img
    ),
    OnboardingPage(
        title = "Customize Your Access",
        description = "Whitelist essential apps to stay connected while minimizing distractions.",
        imageRes = R.drawable.demo_img
    ),
    OnboardingPage(
        title = "Schedule Your Detox",
        description = "Plan your focus sessions ahead of time to build consistent habits.",
        imageRes = R.drawable.demo_img
    ),
    OnboardingPage(
        title = "Your Privacy Matters",
        description = "We prioritize your privacy. All data stays on your device and is never shared.",
        imageRes = R.drawable.demo_img
    )
)

