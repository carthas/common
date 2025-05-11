package com.carthas.common.ui.shader

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush


/**
 * Applies the specified uniform to the runtime shader based on its type.
 *
 * @param uniform The uniform to be applied, which can be an instance of IntUniform, FloatUniform, or ColorUniform.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun RuntimeShader.applyUniform(uniform: Uniform<*>) = when (uniform) {
    is IntUniform -> setIntUniform(uniform.name, uniform.value)
    is FloatUniform -> setFloatUniform(uniform.name, uniform.value)
    is ColorUniform -> setColorUniform(
        uniform.name, android.graphics.Color.valueOf(
            uniform.value.red,
            uniform.value.green,
            uniform.value.blue,
            uniform.value.alpha,
        )
    )
}

/**
 * Applies a custom shader effect to a [Modifier] using the provided [CarthasShader].
 * This shader supports dynamic uniforms that can be updated at runtime.
 *
 * @param shader The [CarthasShader] instance containing the Skia Shading Language (SkSL) code
 *               and a flow of dynamic uniforms to be applied to the shader.
 * @return A [Modifier] that incorporates the effect of the specified shader.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun Modifier.shader(shader: CarthasShader): Modifier = this then composed {
    val runtimeShader = RuntimeShader(shader.skslCode)
    val uniforms by shader.uniformsFlow.collectAsState()

    drawWithCache {
        val shaderBrush = runtimeShader.run {
            setFloatUniform("resolution", size.width, size.height)
            setFloatUniform("density", density)
            uniforms.forEach { applyUniform(it) }
            ShaderBrush(this)
        }

        onDrawBehind {
            drawRect(shaderBrush)
        }
    }
}