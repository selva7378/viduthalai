package com.selvaganesh7378.viduthalai.model

import androidx.annotation.DrawableRes
import com.selvaganesh7378.viduthalai.R

// Data class definition
data class OnboardingPage(
    val title: String,
    val description: String,
    val lottieRes: Int // use raw resource (e.g., R.raw.animation)
)


val onboardingPages = listOf(
    OnboardingPage(
        title = "Welcome to Viduthalai",
        description = "Experience a Viduthalai by locking your device and reclaiming your time.",
        lottieRes = R.raw.viduthalai
    ),
    OnboardingPage(
        title = "Customize Your Access",
        description = "Whitelist essential apps to stay connected while minimizing distractions.",
        lottieRes = R.raw.usage_access
    ),
    OnboardingPage(
        title = "Schedule Your Detox",
        description = "Plan your focus sessions ahead of time to build consistent habits.",
        lottieRes = R.raw.schedule
    ),
    OnboardingPage(
        title = "Your Privacy Matters",
        description = "We prioritize your privacy. All data stays on your device and is never shared.",
        lottieRes = R.raw.privacy
    )
)


