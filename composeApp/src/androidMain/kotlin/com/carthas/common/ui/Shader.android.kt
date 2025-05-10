package com.carthas.common.ui

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
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
 * Converts a [Shader] into a [ShaderBrush] by defining its uniforms and applying the provided size and density
 * parameters as default uniforms. It uses a [RuntimeShader] to process the shader's SkSL code and uniforms.
 *
 * @param size The size of the area to which the shader will be applied, represented as a [Size].
 * @param density The pixel density of the display or rendering context.
 * @return A [ShaderBrush] created based on the configured [Shader].
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun Shader.toShaderBrush(
    size: Size,
    density: Float,
): ShaderBrush = RuntimeShader(this.skslCode)
    .defineUniforms {
        uniform("resolution", size.width, size.height)
        uniform("density", density)
    }
    .defineUniforms(this.defineUniformsBlock)
    .let { ShaderBrush(it) }

/**
 * Adds a custom shader effect to the current [Modifier]. This function applies the provided SkSL-based [Shader]
 * to modify the visual appearance of the content being rendered.
 *
 * @param shader The [Shader] object containing the SkSL code and its uniform configurations to generate the visual effect.
 * @return A [Modifier] with the applied shader effect.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun Modifier.shader(shader: Shader): Modifier = drawWithCache {
    val shaderBrush = shader.toShaderBrush(size, density)
    onDrawBehind {
        drawRect(shaderBrush)
    }
}