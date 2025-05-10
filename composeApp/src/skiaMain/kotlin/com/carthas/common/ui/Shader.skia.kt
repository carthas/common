package com.carthas.common.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder


/**
 * Provides an implementation of the [ShaderUniformProvider] interface specific to Skia's runtime shaders.
 *
 * @constructor Initializes the provider with a given [RuntimeShaderBuilder] instance.
 *
 * @param runtimeShaderBuilder The builder used for defining and modifying shader uniforms.
 */
private class SkiaShaderUniformProvider(
    private val runtimeShaderBuilder: RuntimeShaderBuilder,
) : ShaderUniformProvider {
    /**
     * Sets a uniform variable in the shader with a specified color value.
     *
     * @param name The name of the uniform variable in the shader program.
     * @param value The color value to assign to the uniform variable.
     */
    override fun uniform(name: String, value: Color) = uniform(name, value.red, value.green, value.blue, value.alpha)

    /**
     * Sets uniform data for the target shader with a variable number of integer values.
     *
     * @param name The name of the uniform variable in the shader program.
     * @param values A variable number of integer values to assign to the uniform variable.
     * @throws IllegalArgumentException If no values are provided or if more than four values are provided.
     */
    override fun uniform(name: String, vararg values: Int) = when (values.size) {
        0 -> throwEmpty()
        1 -> runtimeShaderBuilder.uniform(name, values[0])
        2 -> runtimeShaderBuilder.uniform(name, values[0], values[1])
        3 -> runtimeShaderBuilder.uniform(name, values[0], values[1], values[2])
        4 -> runtimeShaderBuilder.uniform(name, values[0], values[1], values[2], values[3])
        else -> throwTooManyArgs()
    }

    /**
     * Sets uniform data for the shader program with a given name and a variable number of float values.
     *
     * @param name The name of the uniform variable in the shader program.
     * @param values A variable number of float values to assign to the uniform variable.
     * Throws an exception if no values are provided or if the number of values exceeds four.
     */
    override fun uniform(name: String, vararg values: Float) = when (values.size) {
        0 -> throwEmpty()
        1 -> runtimeShaderBuilder.uniform(name, values[0])
        2 -> runtimeShaderBuilder.uniform(name, values[0], values[1])
        3 -> runtimeShaderBuilder.uniform(name, values[0], values[1], values[2])
        4 -> runtimeShaderBuilder.uniform(name, values[0], values[1], values[2], values[3])
        else -> throwTooManyArgs()
    }

    /**
     * Throws an exception indicating that at least one value must be provided for SkSL uniform variables.
     *
     * @return This function does not return as it always throws an exception.
     * @throws IllegalArgumentException Always thrown with a message indicating the error.
     */
    private fun throwEmpty(): Nothing = throw IllegalArgumentException("must provide at least 1 value for SkSL uniform")

    /**
     * Throws an exception indicating that too many arguments were provided for a uniform.
     *
     * @return This method never returns as it always throws an exception.
     * @throws IllegalArgumentException Always thrown with a message indicating the error.
     */
    private fun throwTooManyArgs(): Nothing = throw IllegalArgumentException("SkSL only supports up to vec4 uniforms")
}

/**
 * Allows for defining and configuring uniform variables within a runtime shader builder.
 *
 * @param defineUniforms A lambda function where uniform variables can be defined using a [ShaderUniformProvider].
 * @return The original [RuntimeShaderBuilder] instance for method chaining.
 */
private fun RuntimeShaderBuilder.defineUniforms(
    defineUniforms: ShaderUniformProvider.() -> Unit,
): RuntimeShaderBuilder = SkiaShaderUniformProvider(this)
    .defineUniforms()
    .let { this }

/**
 * Converts this [Shader] instance into a [RenderEffect] using the specified [Size].
 *
 * The method creates a runtime effect from the shader's SkSL code, configures shader uniforms,
 * and builds a runtime shader with the provided size. This is then used to create an image filter
 * which is converted to a [RenderEffect] compatible with Compose.
 *
 * @param size The size object containing the width and height used to define the resolution for the shader.
 * @return A [RenderEffect] created from the given [Shader], which can be applied in Compose rendering layers.
 */
private fun Shader.toRenderEffect(size: Size): RenderEffect {
    val runtimeEffect = RuntimeEffect.makeForShader(
        sksl = this.skslCode,
    )
    val runtimeShader = RuntimeShaderBuilder(effect = runtimeEffect)
        .defineUniforms(this@toRenderEffect.defineUniformsBlock)
        .defineUniforms { resolution(size) }
        .makeShader()
    val imageFilter = ImageFilter.makeShader(
        shader = runtimeShader,
        crop = null,
    )
    return imageFilter.asComposeRenderEffect()
}

/**
 * Adds a custom shader to the current Modifier. This function applies the provided SkSL shader using the Skia runtime
 * environment to modify the rendering behavior of the content it's applied to.
 *
 * @param shader The Shader object defining the SkSL (Skia Shading Language) code and its associated uniform configuration.
 * @return A Modifier with the provided shader effect applied.
 */
actual fun Modifier.shader(
    shader: Shader,
): Modifier = this then composed {
    graphicsLayer {
        clip = true
        renderEffect = shader.toRenderEffect(size)
    }
}