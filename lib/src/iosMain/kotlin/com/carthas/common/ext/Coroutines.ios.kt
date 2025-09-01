package com.carthas.common.ext

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO


internal actual val ioBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

internal actual val cpuBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default