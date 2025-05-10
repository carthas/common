package com.carthas.common.ui

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
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
 * Applies a custom shader to the current modifier by utilizing the provided shader configuration by using Android
 * AGSL runtime APIs.
 *
 * @param shader The Shader object containing the SkSL(AGSL) code and uniform definitions to apply.
 * @return A Modifier with the custom shader applied.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun Modifier.shader(shader: Shader): Modifier = drawWithCache {
    val runtimeShader = RuntimeShader(shader.skslCode)
        .defineUniforms { resolution(size) }
        .defineUniforms(shader.defineUniformsBlock)
    val shaderBrush = ShaderBrush(runtimeShader)
    onDrawBehind {
        drawRect(shaderBrush)
    }
}