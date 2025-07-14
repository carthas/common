package com.carthas.common.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


internal actual val ioBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

internal actual val cpuBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default