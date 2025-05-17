package com.carthas.common.ui.shader

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer


/**
 * Adds a custom SkSL shader to the [Modifier] pipeline.
 *
 * @param carthasShader The [CarthasShader] instance containing the shader's SkSL code, static uniforms,
 * and time as state.
 * @return The modified [Modifier] with the specified shader effect applied.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun Modifier.shader(carthasShader: CarthasShader): Modifier = composed {
    val time by carthasShader.currentTime
    val runtimeShader = rememberRuntimeShader(carthasShader)

    var lastSize by remember { mutableStateOf(Size.Unspecified) }
    var lastDensity by remember { mutableStateOf(-1f) }
    var lastTime by remember { mutableStateOf(-1f) }

    graphicsLayer {
        val needsUpdate = size != lastSize ||
                density != lastDensity ||
                time != lastTime
        fun updateLastValues() {
            lastSize = size
            lastDensity = density
            lastTime = time
        }
        fun RuntimeShader.updateUniforms() {
            setFloatUniform("resolution", size.width, size.height)
            setFloatUniform("density", density)
            setFloatUniform("time", time)
        }

        if (needsUpdate) {
            runtimeShader.updateUniforms()
            updateLastValues()
        }

        clip = true
        renderEffect = runtimeShader.toRenderEffect()
    }
}

/**
 * Creates and remembers a [RuntimeShader] based on the given [CarthasShader].
 * The function applies all static uniforms defined in the provided [CarthasShader]
 * to the created [RuntimeShader].
 *
 * @param carthasShader An instance of [CarthasShader] containing the SkSL shader code
 * and related static uniforms.
 * @return A remembered [RuntimeShader] instance.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun rememberRuntimeShader(carthasShader: CarthasShader): RuntimeShader = remember {
    RuntimeShader(carthasShader.skslCode).apply {
        carthasShader.staticUniforms.forEach {
            applyUniform(it)
        }
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
        uniform.name, android.graphics.Color.valueOf(
            uniform.value.red,
            uniform.value.green,
            uniform.value.blue,
            uniform.value.alpha,
        )
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