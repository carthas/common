package com.carthas.common.ext


/**
 * Casts the receiver to the specified type [T].
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> Any.casted() = this as T