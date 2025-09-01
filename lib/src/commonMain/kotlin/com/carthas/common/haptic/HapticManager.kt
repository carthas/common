package com.carthas.common.haptic

import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.time.Duration



/**
 * A [androidx.compose.runtime.CompositionLocal] that provides access to a [HapticManager] within a composable hierarchy.
 * The [HapticManager] allows triggering haptic feedback (vibrations) from Composable functions on supporting devices.
 */
val LocalHapticManager = staticCompositionLocalOf<HapticManager> { error("No HapticManager provided") }

/**
 * Manager interface for handling device haptic feedback.
 * Provides a unified API to trigger vibrations across different platforms.
 */
interface HapticManager {

    /**
     * Triggers a haptic vibration with the specified parameters.
     *
     * @param duration The duration of the vibration. Defaults to [Duration.ZERO], indicating a transient vibration.
     * @param intensity The intensity of the vibration, represented as a float value where 0.0 is no intensity
     * and 1.0 is maximum intensity.
     * @param sharpness The sharpness of the vibration, represented as a float value where 0.0 is the least sharp and
     * 1.0 is the sharpest. (currently only supported on iOS)
     */
    suspend fun vibrate(
        duration: Duration = Duration.ZERO,
        intensity: Float,
        sharpness: Float,
    )
}