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


private class SkiaShaderUniformProvider(
    private val runtimeShaderBuilder: RuntimeShaderBuilder,
) : ShaderUniformProvider {
    override fun uniform(name: String, value: Color) = uniform(name, value.red, value.green, value.blue)

    override fun uniform(name: String, vararg values: Int) = when (values.size) {
        0 -> throwEmpty()
        1 -> runtimeShaderBuilder.uniform(name, values[0])
        2 -> runtimeShaderBuilder.uniform(name, values[0], values[1])
        3 -> runtimeShaderBuilder.uniform(name, values[0], values[1], values[2])
        4 -> runtimeShaderBuilder.uniform(name, values[0], values[1], values[2], values[3])
        else -> throwTooManyArgs()
    }

    override fun uniform(name: String, vararg values: Float) = when (values.size) {
        0 -> throwEmpty()
        1 -> runtimeShaderBuilder.uniform(name, values[0])
        2 -> runtimeShaderBuilder.uniform(name, values[0], values[1])
        3 -> runtimeShaderBuilder.uniform(name, values[0], values[1], values[2])
        4 -> runtimeShaderBuilder.uniform(name, values[0], values[1], values[2], values[3])
        else -> throwTooManyArgs()
    }

    private fun throwEmpty(): Nothing = throw IllegalArgumentException("must provide at least 1 value for SkSL uniform")
    private fun throwTooManyArgs(): Nothing = throw IllegalArgumentException("SkSL only supports up to vec4 uniforms")
}

private fun RuntimeShaderBuilder.defineUniforms(
    defineUniforms: ShaderUniformProvider.() -> Unit,
): RuntimeShaderBuilder = SkiaShaderUniformProvider(this)
    .defineUniforms()
    .let { this }

private fun Shader.toRenderEffect(size: Size): RenderEffect {
    val runtimeEffect = RuntimeEffect.makeForShader(
        sksl = this.code,
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

actual fun Modifier.shader(
    shader: Shader,
): Modifier = this then composed {
    graphicsLayer {
        clip = true
        renderEffect = shader.toRenderEffect(size)
    }
}