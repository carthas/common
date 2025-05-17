package com.carthas.common.ext

import arrow.core.toOption

/**
 * A utility method that transforms a nullable value into a result using the provided
 * handling functions. Executes either the [ifNull] function when the value is `null`
 * or the [ifPresent] function when the value is non-null.
 *
 * @param T The type of the nullable value.
 * @param R The type of the result produced by the transformation functions.
 * @param ifNull A function to be executed when the value is `null`. Produces a result of type [R].
 * @param ifPresent A function to be executed when the value is non-null. Takes the non-null value as input and produces a result of type [R].
 * @return The result of applying either [ifNull] or [ifPresent].
 */
inline fun <T : Any?, R> T.fold(
    ifNull: () -> R,
    ifPresent: (T & Any) -> R,
) = this.toOption().fold(
    ifEmpty = ifNull,
    ifSome = ifPresent,
)