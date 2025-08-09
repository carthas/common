package com.carthas.common.haptic

import com.carthas.common.ext.isNotNull
import com.carthas.common.ext.isNull
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreHaptics.CHHapticEngine
import platform.CoreHaptics.CHHapticEvent
import platform.CoreHaptics.CHHapticEventParameter
import platform.CoreHaptics.CHHapticEventParameterIDHapticIntensity
import platform.CoreHaptics.CHHapticEventParameterIDHapticSharpness
import platform.CoreHaptics.CHHapticEventTypeHapticContinuous
import platform.CoreHaptics.CHHapticEventTypeHapticTransient
import platform.CoreHaptics.CHHapticPattern
import kotlin.time.Duration
import kotlin.time.DurationUnit


class IOSHapticManager : HapticManager {

    private var isEngineRunning = false

    @OptIn(ExperimentalForeignApi::class)
    private val engine = CHHapticEngine(andReturnError = null)
        .apply {
            setResetHandler {
                println("HapticService engine reset")
                isEngineRunning = false
            }
            setStoppedHandler {
                println("HapticService engine stopped. reason: $it")
                isEngineRunning = false
            }
            startWithCompletionHandler { error ->
                if (error.isNotNull()) println("HapticService engine start failed. error: ${error.description}")
                else isEngineRunning = true
            }
        }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun vibrate(
        duration: Duration,
        intensity: Float,
        sharpness: Float,
    ) {
        if (!isEngineRunning) startEngine()
        if (!isEngineRunning) {
            println("HapticService engine could not be started")
            return
        }

        val params = listOf(
            CHHapticEventParameter(
                parameterID = CHHapticEventParameterIDHapticIntensity,
                value = intensity,
            ),
            CHHapticEventParameter(
                parameterID = CHHapticEventParameterIDHapticSharpness,
                value = sharpness,
            ),
        )

        val hapticEvent =
            if (duration == Duration.ZERO) CHHapticEvent(
                eventType = CHHapticEventTypeHapticTransient,
                parameters = params,
                relativeTime = 0.0,
            )
            else CHHapticEvent(
                eventType = CHHapticEventTypeHapticContinuous,
                parameters = params,
                relativeTime = 0.0,
                duration = duration.toDouble(unit = DurationUnit.SECONDS),
            )

        val chPattern = CHHapticPattern(
            events = listOf(hapticEvent),
            parameters = emptyList<Unit>(),
            error = null,
        )

        val player = engine.createPlayerWithPattern(
            pattern = chPattern,
            error = null,
        )

        if (player.isNull()) println("HapticService vibrate error: player is null")
        else player.startAtTime(
            time = 0.0,
            error = null,
        )

        delay(duration)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun startEngine() = suspendCancellableCoroutine { continuation ->
        engine.startWithCompletionHandler { error ->
            if (error.isNotNull()) println("HapticService engine start failed. error: ${error.description}")
            else isEngineRunning = true
            continuation.resume(Unit) { println("HapticService engine start cancelled") }
        }
    }
}