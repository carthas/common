package com.carthas.common.ui.shader

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder
import org.jetbrains.skia.Shader


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
 * Converts an instance of a Skia [CarthasShader] object into a [RenderEffect] that can be used
 * to apply custom rendering effects within the rendering pipeline.
 *
 * @return A [RenderEffect] derived from the current [CarthasShader], enabling the integration of the
 *         shader into the composition's rendering process for visual customization.
 */
private fun Shader.toRenderEffect(): RenderEffect = ImageFilter.makeShader(
    shader = this,
    crop = null,
).asComposeRenderEffect()

/**
 * Adds a shader effect to this [Modifier] by applying the provided [CarthasShader]. This custom shader
 * supports dynamic updates to uniform variables.
 *
 * @param shader The [CarthasShader] instance that contains SkSL code and a flow of dynamic uniforms to be applied
 *               to the [RuntimeShaderBuilder].
 * @return A [Modifier] that integrates the specified shader effect into the rendering pipeline.
 */
actual fun Modifier.shader(
    shader: CarthasShader,
): Modifier = this then composed {
    val runtimeEffect = RuntimeEffect.makeForShader(sksl = shader.skslCode)
    val uniforms by shader.uniformsFlow.collectAsState()

    graphicsLayer {
        clip = true
        renderEffect = RuntimeShaderBuilder(runtimeEffect)
            .apply {
                uniform("resolution", size.width, size.height)
                uniform("density", density)
                uniforms.forEach { applyUniform(it) }
            }
            .makeShader()
            .toRenderEffect()
    }
}