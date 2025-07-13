package com.carthas.common.mvi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


@PublishedApi
internal actual val ioBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default

@PublishedApi
internal actual val cpuBoundDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default