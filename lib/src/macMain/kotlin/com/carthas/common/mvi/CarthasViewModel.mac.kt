package com.carthas.common.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO


internal actual val ioBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

internal actual val cpuBoundDispatcher: kotlinx.coroutines.CoroutineDispatcher
    get() = Dispatchers.Default