package com.carthas.common.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color


/**
 * Interface providing a mechanism to define and supply uniform values
 * for shaders. A uniform is a global variable in the shader accessible to both
 * the vertex and the fragment shader, and its value remains constant for the
 * duration of a single render call.
 */
interface ShaderUniformProvider {
    /**
     * Sets a uniform variable for the shader program with a given name and a color value.
     *
     * @param name The name of the uniform variable in the shader program.
     * @param value The color value to assign to the uniform variable.
     */
    fun uniform(name: String, value: Color)

    /**
     * Sets uniform data for the target shader with a variable number of integers.
     *
     * @param name The name of the uniform variable in the shader program.
     * @param values One or more integer values to assign to the uniform variable.
     */
    fun uniform(name: String, vararg values: Int)

    /**
     * Sets uniform data for the target shader with a variable number of floats.
     *
     * @param name The name of the uniform variable in the shader program.
     * @param values One or more float values to assign to the uniform variable.
     */
    fun uniform(name: String, vararg values: Float)

    /**
     * Sets the resolution uniform for a shader using the specified size.
     *
     * @param size The size containing the width and height used to define the resolution.
     */
    fun resolution(size: Size) = uniform("resolution", size.width, size.height)
}

/**
 * Represents a shader program defined by SkSL (Skia Shading Language) code, along with a block to define uniform
 * variables for the shader.
 *
 * @param skslCode The SkSL code defining the shader program. This code is passed directly to the rendering pipeline
 * and adheres to SkSL standards.
 * @param defineUniformsBlock A lambda that enables the configuration of shader uniforms.
 */
open class Shader(
    val skslCode: String,
    val defineUniformsBlock: ShaderUniformProvider.() -> Unit,
)

/**
 * Adds a custom shader layer to the current modifier.
 *
 * @param shader The Shader object containing the SkSL code and uniform definitions to be applied.
 * @return A Modifier with the applied shader.
 */
expect fun Modifier.shader(
    shader: Shader,
): Modifier

