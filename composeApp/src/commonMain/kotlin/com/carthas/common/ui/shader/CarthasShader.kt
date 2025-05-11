package com.carthas.common.ui.shader

import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


/**
 * Represents a wrapper around a custom Skia Shading Language (SkSL) shader with support for dynamic
 * uniform updates. This class is designed to assist in dynamically providing uniforms to a shader during runtime.
 *
 * @property skslCode The SkSL code defining the shader logic.
 * @property uniformsFlow A flow of uniforms that dynamically updates as new uniform values are set.
 */
class CarthasShader(
    val skslCode: String,
    initialUniforms: Set<Uniform<*>>,
) {
    /**
     * A mutable state flow that holds the current set of uniforms for the shader.
     * It is initialized with the provided set of uniforms and can be updated dynamically.
     * This flow allows tracking real-time changes to the uniforms and propagates them to any
     * observers, including rendering pipelines or shaders that depend on this data.
     */
    private val _uniformsFlow = MutableStateFlow(initialUniforms)
    /**
     * A state flow that emits the current set of uniform variables used by the shader. Uniforms
     * are a collection of configurable parameters that define the behavior or appearance of
     * the shader.
     *
     * This flow allows real-time updates to the shader's uniform variables.
     */
    val uniformsFlow = _uniformsFlow.asStateFlow()

    /**
     * Updates the shader's uniform set with the provided uniform. If a uniform
     * with the same name already exists, it is replaced. Otherwise, the new uniform
     * is added to the set.
     *
     * @param uniform The uniform to be added or updated in the shader's uniform set.
     */
    suspend fun setUniform(uniform: Uniform<Any>) = _uniformsFlow.value.let { currentUniforms ->
        // true if there is no uniform matching both name and value
        if (!currentUniforms.contains(uniform)) {
            val newUniforms = currentUniforms
                .filter { it.name != uniform.name }
                .toSet() + uniform
            _uniformsFlow.emit(newUniforms)
        }
    }
}

/**
 * Adds a custom shader layer to the current modifier.
 *
 * @param shader The Shader object containing the SkSL code and uniform definitions to be applied.
 * @return A Modifier with the applied shader.
 */
expect fun Modifier.shader(
    shader: CarthasShader,
): Modifier

