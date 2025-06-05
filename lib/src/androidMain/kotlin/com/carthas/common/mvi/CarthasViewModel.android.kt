package com.carthas.common.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


actual val ioBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

actual val cpuBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default