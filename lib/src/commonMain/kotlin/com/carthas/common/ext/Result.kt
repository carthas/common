package com.carthas.common.ext

import arrow.core.nonFatalOrThrow


inline fun <T> runCatchingSafe(block: () -> T): Result<T> =
    runCatching { block() }
        .onFailure { it.nonFatalOrThrow() }

inline fun <T, R> T.runCatchingSafe(block: T.() -> R): Result<R> =
    runCatching { block() }
        .onFailure { it.nonFatalOrThrow() }