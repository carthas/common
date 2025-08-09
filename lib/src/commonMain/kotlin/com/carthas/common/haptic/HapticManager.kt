package com.carthas.common.haptic

import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.time.Duration


val LocalHapticManager = staticCompositionLocalOf<HapticManager> { error("No HapticManager provided") }

interface HapticManager {
    suspend fun vibrate(
        duration: Duration = Duration.ZERO,
        intensity: Float,
        sharpness: Float,
    )
}