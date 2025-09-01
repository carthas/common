package com.carthas.common.ext

import arrow.core.nonFatalOrThrow


/**
 * Executes the given [block], catching any non-fatal exception that occurs.
 *
 * @param block The block of code to be executed safely.
 * @return A [Result] representing either the successful outcome of [block] or the caught exception.
 */
inline fun <T> runCatchingSafe(block: () -> T): Result<T> =
    runCatching { block() }
        .onFailure { it.nonFatalOrThrow() }

/**
 * Executes the given [block] of suspending code, catching any non-fatal exception that occurs.
 *
 * @param block A lambda function with receiver of type [T] to execute.
 * @return A [Result] object representing either the successful result of [block] or the caught exception.
 */
inline fun <T, R> T.runCatchingSafe(block: T.() -> R): Result<R> =
    runCatching { block() }
        .onFailure { it.nonFatalOrThrow() }