package com.carthas.common.ui.shader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.carthas.common.ext.fold
import com.carthas.common.ui.AnimationTimeProducer
import kotlin.jvm.JvmInline


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class CompiledShader

/**
 * Adds a custom SkSL shader to the [Modifier] pipeline.
 *
 * @param carthasShader The [CarthasShader] instance containing the shader's SkSL code, static uniforms,
 * and time as state.
 * @return The modified [Modifier] with the specified shader effect applied.
 */
expect fun Modifier.shader(carthasShader: CarthasShader): Modifier

/**
 * Represents a custom Skia Shading Language (SkSL) shader that can be used in
 * Compose UI to apply complex graphical effects to UI components.
 *
 * @property skSLCode A string containing the SkSL shader code.
 *
 * @property staticUniforms A set of static [Uniform] parameters used by the shader.
 *
 * @property animationTimeProducer Optional time producer that provides a stateful
 * flow of time values which can be used within the shader logic to create
 * time-based animations.
 */
@Immutable
class CarthasShader(
    val skSLCode: String,
    val staticUniforms: UniformSet = UniformSet.Empty,
    val animationTimeProducer: AnimationTimeProducer? = null,
) {
    /**
     * The shader's `time` input.
     *
     * - If [animationTimeProducer] is present, this is a dynamically updated state with the produced time.
     * - If not, it is just `0f`.
     */
    @Stable
    @get:Composable
    val currentTime: State<Float>
        get() = animationTimeProducer.fold(
            { remember { mutableStateOf(0f) } },
            { it.collectTimeAsState() },
        )
}

/**
 * A wrapper class for a set of shader uniform definitions. This class is used as a [remember] key for compositional caching.
 *
 * @property uniforms The set of [Uniform] instances.
 */
@JvmInline
@Immutable
value class UniformSet(val uniforms: Set<Uniform<*>>) {

    companion object {
        /**
         * Represents an empty [UniformSet] with no uniforms.
         * Useful as a default or placeholder value when no uniforms are needed.
         */
        val Empty = UniformSet(emptySet())
    }
}