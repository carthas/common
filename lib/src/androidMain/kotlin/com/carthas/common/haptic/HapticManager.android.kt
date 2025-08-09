package com.carthas.common.haptic

import android.content.Context
import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.S
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.carthas.common.isMinSDK
import kotlin.time.Duration


class AndroidHapticManager(context: Context) : HapticManager {

    private val vibrator =
        if (isMinSDK(S)) (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        else context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    override suspend fun vibrate(
        duration: Duration,
        intensity: Float,
        sharpness: Float,  // sharpness is ignored on Android (not supported)
    ) {
        if (!vibrator.hasVibrator()) return

        if (isMinSDK(O)) {
            val amplitude = (intensity * 255)
                .toInt()
                .coerceIn(1, 255)

            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    duration.inWholeMilliseconds,
                    amplitude,
                ),
            )
        } else @Suppress("DEPRECATION") vibrator.vibrate(duration.inWholeMilliseconds)  // simple vibration for older devices
    }
}