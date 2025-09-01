package com.carthas.common.ext

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


/**
 * The current moment in time as an [Instant].
 */
@OptIn(ExperimentalTime::class)
val now: Instant
    get() = Clock.System.now()

/**
 * The current date based on the system's default time zone.
 */
@OptIn(ExperimentalTime::class)
val today: LocalDate
    get() = Clock.System.todayIn(TimeZone.currentSystemDefault())