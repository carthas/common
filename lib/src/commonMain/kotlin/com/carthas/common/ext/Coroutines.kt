package com.carthas.common.ext

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext


internal expect val ioBoundDispatcher: CoroutineDispatcher

internal expect val cpuBoundDispatcher: CoroutineDispatcher

/**
 * Executes IO-bound work within the context of [ioBoundDispatcher], in this [viewModelScope].
 *
 * @param T The type of the result returned by [lambda].
 * @param lambda A suspending lambda function representing the IO-bound operation to be executed.
 * @return The result of the operation defined in [lambda].
 */
suspend fun <T> ioBound(lambda: suspend CoroutineScope.() -> T): T =
    withContext(context = ioBoundDispatcher, lambda)

/**
 * Executes CPU-bound work within the context of [cpuBoundDispatcher], in this [viewModelScope].
 *
 * @param T The return type of the operation performed by [lambda].
 * @param lambda A suspending function block to be executed on the CPU-bound dispatcher.
 * @return The result of the operation defined in [lambda].
 */
suspend fun <T> cpuBound(lambda: suspend CoroutineScope.() -> T) =
    withContext(context = cpuBoundDispatcher, lambda)