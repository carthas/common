package com.carthas.common.ui

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import kotlin.IllegalArgumentException


/**
 * Implementation of the [ShaderUniformProvider] interface that provides uniform values
 * for shaders using the Android [RuntimeShader] API.
 *
 * @constructor Creates an AndroidShaderUniformProvider with a [RuntimeShader] instance.
 * @param runtimeShader The [RuntimeShader] instance to which uniform values are applied.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class AndroidShaderUniformProvider(
    private val runtimeShader: RuntimeShader,
) : ShaderUniformProvider {
    override fun uniform(name: String, value: Color) = runtimeShader.setColorUniform(
        name,
        android.graphics.Color.valueOf(
            value.red,
            value.green,
            value.blue,
            value.alpha,
        )
    )

    /**
     * Sets uniform data for the target shader with a variable number of integers.
     *
     * @param name The name of the uniform variable in the shader program.
     * @param values A variable number of integer values to assign to the uniform variable.
     * @throws IllegalArgumentException if no values are provided.
     */
    override fun uniform(name: String, vararg values: Int) = when (values.size) {
        0 -> throwEmpty()
        else -> runtimeShader.setIntUniform(name, values)
    }

    /**
     * Sets uniform data for the target shader with a variable number of floats.
     *
     * @param name The name of the uniform variable in the shader program.
     * @param values One or more float values to assign to the uniform variable.
     * @throws IllegalArgumentException if no values are provided.
     */
    override fun uniform(name: String, vararg values: Float) = when (values.size) {
        0 -> throwEmpty()
        else -> runtimeShader.setFloatUniform(name, values)
    }

    /**
     * Throws an [IllegalArgumentException] indicating that at least one value must be provided
     * for the SkSL uniform.
     *
     * @return This method never returns as it always throws an exception.
     */
    private fun throwEmpty(): Nothing = throw IllegalArgumentException("must provide at least 1 value for AGSL uniform")
}

/**
 * Defines uniforms for a [RuntimeShader] using the provided lambda.
 *
 * @param defineUniforms A lambda that defines and set uniform values for the shader.
 * @return The same [RuntimeShader] instance with the uniforms configured.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun RuntimeShader.defineUniforms(
    defineUniforms: ShaderUniformProvider.() -> Unit,
): RuntimeShader = AndroidShaderUniformProvider(this)
    .defineUniforms()
    .let { this }

/**
 * Defines dynamic uniforms for a [RuntimeShader] using the provided composable lambda.
 *
 * @param defineDynamicUniforms A composable lambda function that defines and sets dynamic
 * uniform values for the shader.
 * @return The same [RuntimeShader] instance with the dynamic uniforms configured.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun RuntimeShader.defineDynamicUniforms(
    defineDynamicUniforms: @Composable ShaderUniformProvider.() -> Unit,
): RuntimeShader = AndroidShaderUniformProvider(this)
    .defineDynamicUniforms()
    .let { this }

/**
 * Converts the current [Shader] instance to a [RuntimeShader] object, enabling the use
 * of its SkSL code and uniform configuration for rendering. This function uses the
 * defined blocks of static and dynamic uniform configurations from the [Shader]
 * to initialize the resulting [RuntimeShader].
 *
 * @return A [RuntimeShader] instance configured with the SkSL code and uniform settings
 * from this [Shader].
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun Shader.toRuntimeShader(): RuntimeShader = RuntimeShader(this.skslCode)
    .defineUniforms(this.defineUniformsBlock)
    .defineDynamicUniforms(this.defineDynamicUniformsBlock)

/**
 * Converts a [RuntimeShader] into a [ShaderBrush] after defining required uniforms.
 *
 * @param size The dimensions of the target area on which the shader will be drawn.
 * @param density The display density to be used as a uniform for the shader.
 * @return A [ShaderBrush] instance for use in rendering.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun RuntimeShader.toShaderBrush(
    size: Size,
    density: Float,
): ShaderBrush = this
    .defineUniforms {
        uniform("resolution", size.width, size.height)
        uniform("density", density)
    }
    .let { ShaderBrush(it) }

/**
 * Applies a custom SkSL-based shader to the current [Modifier]. The provided [Shader] defines both
 * the shader program and the associated uniform variables to customize the visual rendering.
 *
 * @param shader The [Shader] containing the SkSL program and uniform definitions used to create a
 *               runtime shader. This shader is used to draw using a custom [ShaderBrush].
 * @return A [Modifier] that applies the specified shader effect during drawing.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun Modifier.shader(shader: Shader): Modifier = this then composed {
    val runtimeShader = shader.toRuntimeShader()
    drawWithCache {
        val shaderBrush = runtimeShader.toShaderBrush(size, density)
        onDrawBehind {
            drawRect(shaderBrush)
        }
    }
}