package com.carthas.common.ui

import androidx.compose.runtime.Composable
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
 * Defines dynamic uniforms for a runtime shader using the given composable block.
 *
 * @param defineDynamicUniforms A composable lambda function invoked within a [ShaderUniformProvider] context
 * to dynamically define shader uniform variables.
 * @return The same instance of [RuntimeShaderBuilder] after defining the dynamic uniforms.
 */
@Composable
private fun RuntimeShaderBuilder.defineDynamicUniforms(
    defineDynamicUniforms: @Composable ShaderUniformProvider.() -> Unit,
): RuntimeShaderBuilder = SkiaShaderUniformProvider(runtimeShaderBuilder = this)
    .defineDynamicUniforms()
    .let { this }

/**
 * Converts a [Shader] instance into a [RuntimeShaderBuilder] by compiling the shader's SkSL code
 * into a runtime effect and applying uniform configurations.
 *
 * The method initializes a [RuntimeEffect] using the SkSL code defined in the [Shader], and then
 * uses the [Shader.defineUniformsBlock] and [Shader.defineDynamicUniformsBlock] to configure
 * static and dynamic uniforms.
 *
 * @return A [RuntimeShaderBuilder] with the compiled runtime effect and uniform configurations
 *         defined by the given [Shader].
 */
@Composable
private fun Shader.toRuntimeShaderBuilder(): RuntimeShaderBuilder {
    val runtimeEffect = RuntimeEffect.makeForShader(
        sksl = this.skslCode,
    )
    return RuntimeShaderBuilder(effect = runtimeEffect)
        .defineUniforms(this@toRuntimeShaderBuilder.defineUniformsBlock)
        .defineDynamicUniforms(this@toRuntimeShaderBuilder.defineDynamicUniformsBlock)
}

/**
 * Builds and returns an instance of a Skia shader using predefined uniform values for resolution and density.
 *
 * @param size The dimensions of the target rendering area, where `size.width` and `size.height`
 *             specify the width and height respectively.
 * @param density The pixel density of the rendering target, typically used to adjust
 *                the shader's behavior based on the display's logical density.
 * @return A Skia Shader object configured with specified uniform values.
 */
private fun RuntimeShaderBuilder.build(
    size: Size,
    density: Float,
): org.jetbrains.skia.Shader = this
    .defineUniforms {
        uniform("resolution", size.width, size.height)
        uniform("density", density)
    }
    .makeShader()

/**
 * Converts an instance of a Skia [Shader] object into a [RenderEffect] that can be used
 * to apply custom rendering effects within the rendering pipeline.
 *
 * @return A [RenderEffect] derived from the current [Shader], enabling the integration of the
 *         shader into the composition's rendering process for visual customization.
 */
private fun org.jetbrains.skia.Shader.toRenderEffect(): RenderEffect = ImageFilter.makeShader(
    shader = this,
    crop = null,
).asComposeRenderEffect()

/**
 * Applies a custom shader effect to the current Modifier. The shader is defined by a SkSL-based [Shader],
 * and this method integrates the shader into the rendering pipeline to alter the visual appearance of the content.
 *
 * @param shader The [Shader] object containing SkSL code and uniform definitions that describe the custom shader effect.
 * @return A [Modifier] with the specified shader effect applied.
 */
actual fun Modifier.shader(
    shader: Shader,
): Modifier = this then composed {
    val runtimeShaderBuilder = shader.toRuntimeShaderBuilder()
    graphicsLayer {
        clip = true
        renderEffect = runtimeShaderBuilder
            .build(
                size = size,
                density = density,
            )
            .toRenderEffect()
    }
}