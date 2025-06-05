package com.carthas.common.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


actual val ioBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default

actual val cpuBoundDispatcher: kotlinx.coroutines.CoroutineDispatcher
    get() = Dispatchers.Default