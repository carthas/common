package com.carthas.common.ui.shader

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import com.carthas.common.ext.orElse


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual typealias CompiledShader = RuntimeShader

private val compiledShaderCache = mutableMapOf<String, CompiledShader>()

/**
 * Adds a custom SkSL shader to the [Modifier] pipeline.
 *
 * @param carthasShader The [CarthasShader] instance containing the shader's SkSL code, static uniforms,
 * and time as state.
 * @return The [Modifier] with the specified shader effect applied.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun Modifier.shader(carthasShader: CarthasShader): Modifier = composed {
    val time by carthasShader.currentTime
    val runtimeShader = remember(carthasShader.skSLCode) {
        compiledShaderCache[carthasShader.skSLCode]
            .orElse {
                val compiledShader = RuntimeShader(carthasShader.skSLCode)
                compiledShaderCache[carthasShader.skSLCode] = compiledShader
                compiledShader
            }
    }
    val shaderWithStaticUniforms = remember(carthasShader.skSLCode, carthasShader.staticUniforms) {
        runtimeShader.apply {
            carthasShader.staticUniforms.uniforms.forEach { applyUniform(it) }
        }
    }

    graphicsLayer {
        shaderWithStaticUniforms.apply {
            setFloatUniform("resolution", size.width, size.height)
            setFloatUniform("density", density)
            setFloatUniform("time", time)
        }

        clip = true
        renderEffect = shaderWithStaticUniforms.toRenderEffect()
    }
}

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
        uniform.name,
        android.graphics.Color.valueOf(
            uniform.value.red,
            uniform.value.green,
            uniform.value.blue,
            uniform.value.alpha,
        ),
    )
}

/**
 * Converts the android [RuntimeShader] into a Compose [RenderEffect].
 *
 * Provides the previous shader in the pipeline as a shader uniform called `content`.
 *
 * @return A [RenderEffect] object derived from the runtime shader configuration.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun RuntimeShader.toRenderEffect(): RenderEffect = android.graphics.RenderEffect.createRuntimeShaderEffect(
    this,
    "content",
).asComposeRenderEffect()