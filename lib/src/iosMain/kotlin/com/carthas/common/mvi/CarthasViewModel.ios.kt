package com.carthas.common.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO


@PublishedApi
internal actual val ioBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

@PublishedApi
internal actual val cpuBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default