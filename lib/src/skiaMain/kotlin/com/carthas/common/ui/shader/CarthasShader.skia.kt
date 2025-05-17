package com.carthas.common.ui.shader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder


/**
 * Adds a custom SkSL shader to the [Modifier] pipeline.
 *
 * @param carthasShader The [CarthasShader] instance containing the shader's SkSL code, static uniforms,
 * and time as state.
 * @return The modified [Modifier] with the specified shader effect applied.
 */
actual fun Modifier.shader(carthasShader: CarthasShader): Modifier = composed {
    val time by carthasShader.currentTime
    val runtimeEffect = remember { RuntimeEffect.makeForShader(sksl = carthasShader.skslCode) }
    val shaderBuilder = remember {
        RuntimeShaderBuilder(runtimeEffect).apply {
            carthasShader.staticUniforms.forEach {
                applyUniform(it)
            }
        }
    }

    var lastSize by remember { mutableStateOf(Size.Unspecified) }
    var lastDensity by remember { mutableStateOf(-1f) }
    var lastTime by remember { mutableStateOf(-1f) }

    graphicsLayer {
        val needsUpdate = size != lastSize ||
                density != lastDensity ||
                time != lastTime
        fun updateLastValues() {
            lastSize = size
            lastDensity = density
            lastTime = time
        }
        fun RuntimeShaderBuilder.updateUniforms() {
            uniform("resolution", size.width, size.height)
            uniform("density", density)
            uniform("time", time)
        }

        if (needsUpdate) {
            shaderBuilder.updateUniforms()
            updateLastValues()
        }

        clip = true
        renderEffect = shaderBuilder.toRenderEffect()
    }
}

/**
 * Applies a uniform to the shader builder based on its type.
 *
 * @param uniform The uniform to apply to the shader builder. It may be an instance of
 * [IntUniform], [FloatUniform], or [ColorUniform].
 */
private fun RuntimeShaderBuilder.applyUniform(uniform: Uniform<*>) = when (uniform) {
    is IntUniform -> intUniform(uniform.name, uniform.value)
    is FloatUniform -> floatUniform(uniform.name, uniform.value)
    is ColorUniform -> uniform(
        uniform.name,
        uniform.value.red,
        uniform.value.green,
        uniform.value.blue,
        uniform.value.alpha
    )
}

/**
 * Assigns integer uniform values to a shader program. The number of values determines
 * the dimensionality of the uniform being set. Acceptable dimensions are 1, 2, 3, or 4.
 *
 * @param name The name of the uniform variable in the shader program.
 * @param values An array of integers representing the uniform values to set.
 * @throws IllegalArgumentException If the size of the `values` array is not in the range 1 to 4.
 */
private fun RuntimeShaderBuilder.intUniform(
    name: String,
    values: IntArray,
) = when (values.size) {
    1 -> uniform(name, values[0])
    2 -> uniform(name, values[0], values[1])
    3 -> uniform(name, values[0], values[1], values[2])
    4 -> uniform(name, values[0], values[1], values[2], values[3])
    else -> throw IllegalArgumentException("Cannot create a uniform of ${values.size} dimensionality in SkSL")
}

/**
 * Assigns float uniform values to a shader program. The number of values determines
 * the dimensionality of the uniform being set. Acceptable dimensions are 1, 2, 3, or 4.
 *
 * @param name The name of the uniform variable in the shader program.
 * @param values An array of floats representing the uniform values to set.
 * @throws IllegalArgumentException If the size of the `values` array is not in the range 1 to 4.
 */
private fun RuntimeShaderBuilder.floatUniform(
    name: String,
    values: FloatArray,
) = when (values.size) {
    1 -> uniform(name, values[0])
    2 -> uniform(name, values[0], values[1])
    3 -> uniform(name, values[0], values[1], values[2])
    4 -> uniform(name, values[0], values[1], values[2], values[3])
    else -> throw IllegalArgumentException("Cannot create a uniform of ${values.size} dimensionality in SkSL")
}

/**
 * Converts the [RuntimeShaderBuilder] into a [RenderEffect].
 *
 * Provides the previous shader in the pipeline as a shader uniform called `content`.
 *
 * @return A [RenderEffect] object derived from the runtime shader configuration of the builder.
 */
private fun RuntimeShaderBuilder.toRenderEffect(): RenderEffect = ImageFilter.makeRuntimeShader(
    runtimeShaderBuilder = this,
    shaderName = "content",
    input = null,
).asComposeRenderEffect()