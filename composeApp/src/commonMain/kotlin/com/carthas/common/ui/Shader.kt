package com.carthas.common.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color


interface ShaderUniformProvider {
    fun uniform(name: String, value: Color)
    fun uniform(name: String, vararg values: Int)
    fun uniform(name: String, vararg values: Float)
    fun resolution(size: Size) = uniform("resolution", size.width, size.height)
}

open class Shader(
    val code: String,
    val defineUniformsBlock: ShaderUniformProvider.() -> Unit,
)

expect fun Modifier.shader(
    shader: Shader,
): Modifier

