package com.carthas.common.ui

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import kotlin.IllegalArgumentException


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

    override fun uniform(name: String, vararg values: Int) = when (values.size) {
        0 -> throwEmpty()
        1, 2, 3, 4 -> runtimeShader.setIntUniform(name, values)
        else -> throwTooManyArgs()
    }

    override fun uniform(name: String, vararg values: Float) = when (values.size) {
        0 -> throwEmpty()
        1, 2, 3, 4 -> runtimeShader.setFloatUniform(name, values)
        else -> throwTooManyArgs()
    }

    private fun throwEmpty(): Nothing = throw IllegalArgumentException("must provide at least 1 value for SkSL uniform")
    private fun throwTooManyArgs(): Nothing = throw IllegalArgumentException("SkSL only supports up to vec4 uniforms")
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun RuntimeShader.defineUniforms(
    defineUniforms: ShaderUniformProvider.() -> Unit,
): RuntimeShader = AndroidShaderUniformProvider(this)
    .defineUniforms()
    .let { this }

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun Modifier.shader(shader: Shader): Modifier = drawWithCache {
    val runtimeShader = RuntimeShader(shader.code)
        .defineUniforms { resolution(size) }
        .defineUniforms(shader.defineUniformsBlock)
    val shaderBrush = ShaderBrush(runtimeShader)
    onDrawBehind {
        drawRect(shaderBrush)
    }
}