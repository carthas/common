package com.carthas.common.ext

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
val now: Instant
    get() = Clock.System.now()