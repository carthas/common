package com.carthas.common.ui.shader

import androidx.compose.ui.graphics.Color


/**
 * Represents a uniform parameter used in shaders.
 * A uniform is a configurable input parameter that determines the behavior
 * or appearance of a shader. It typically defines properties such as
 * colors, floats, or integers required to render graphics.
 *
 * @param T The type of the uniform's value. Implemented types are [Integer], [Float], and [Color].
 *
 * @property name The unique name identifying the uniform. This name is used
 * to refer to the uniform in shader code.
 *
 * @property value The value of the uniform, representing the actual data
 * being passed to the shader.
 */
sealed interface Uniform<T : Any> {
    val name: String
    val value: T
}

data class IntUniform(
    override val name: String,
    override val value: IntArray,
) : Uniform<IntArray> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as IntUniform

        if (name != other.name) return false
        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}

data class FloatUniform(
    override val name: String,
    override val value: FloatArray,
) : Uniform<FloatArray> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FloatUniform

        if (name != other.name) return false
        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}

data class ColorUniform(
    override val name: String,
    override val value: Color,
) : Uniform<Color>