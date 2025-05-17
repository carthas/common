package com.carthas.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlin.jvm.JvmInline
import kotlin.time.TimeSource


/**
 * A producer for providing time as an input to shaders based on a specified refresh rate.
 * The `ShaderTimeProducer` generates time values that can be utilized to create animations
 * or dynamic effects in shader-based graphics rendering.
 *
 * @param refreshRate The frequency, in hertz (Hz), at which time updates are produced.
 */
@Stable
open class ShaderTimeProducer(refreshRate: Hz) {
    /**
     * [Flow] that emits the elapsed time in seconds since its creation.
     * The emission frequency is determined by [refreshRate].
     *
     * @property timeFlow A periodic flow of elapsed time in seconds, calculated based on the given refresh rate.
     */
    private val timeFlow: Flow<Float> = flow {
        val startTime = TimeSource.Monotonic.markNow()
        val refreshDelaySecs = 1f / refreshRate.value
        val refreshDelayMillis = (1_000 * refreshDelaySecs).toLong()

        while (true) {
            delay(refreshDelayMillis)
            val elapsedSeconds = startTime.elapsedNow().inWholeMilliseconds / 1_000f
            emit(elapsedSeconds)
        }
    }

    /**
     * Collects time updates from the internal [Flow] and emits them to the provided collector.
     *
     * @param collector The [FlowCollector] that will receive the emitted time values as [Float].
     */
    suspend fun collectTime(collector: FlowCollector<Float>) = timeFlow.collect(collector)

    /**
     * Collects the flow of time values from the `timeFlow` property
     * and provides it as a `State<Float>` with an initial value of `0f`.
     *
     * @return A state object containing the current time value as a [Float].
     */
    @Composable
    fun collectTimeAsState() = timeFlow.collectAsState(initial = 0f)

    companion object {
        /**
         * A default instance of [ShaderTimeProducer] with a refresh rate of 60 Hz.
         */
        val Default by lazy { ShaderTimeProducer(refreshRate = 60.Hz) }
    }
}

/**
 * Represents a frequency measured in hertz (Hz).
 *
 * This value class wraps an integer value that denotes the frequency rate.
 * It is commonly used to specify refresh rates or similar periodic metrics.
 *
 * @property value The frequency value in hertz (Hz).
 */
@Immutable
@JvmInline
value class Hz(val value: Int)

/**
 * Extension property to simplify the creation of a [Hz] instance from an [Int].
 *
 * @receiver The integer value representing the frequency in hertz.
 * @return A [Hz] instance initialized with the given integer value.
 */
val Int.Hz: Hz
    get() = Hz(this)

