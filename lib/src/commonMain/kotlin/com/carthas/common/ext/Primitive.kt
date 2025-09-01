package com.carthas.common.ext

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


/**
 * Checks if the instance is null.
 *
 * @return [Boolean] - `true` if the instance is null, `false` otherwise.
 */
@OptIn(ExperimentalContracts::class)
fun Any?.isNull(): Boolean {
    contract {
        returns(false) implies (this@isNull != null)
    }
    return this == null
}

/**
 * Checks if the receiver is not null and returns true if it is not null.
 *
 * @return [Boolean] true if the receiver is not null, false otherwise.
 */
@OptIn(ExperimentalContracts::class)
fun Any?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }
    return this != null
}

/**
 * Returns the value of the nullable receiver if it is not null, otherwise returns [that].
 *
 * @param that the value to return if the receiver is null
 * @return the receiver value if it is non-null, or [that] if the receiver is null
 */
infix fun <T> T?.orElse(that: T): T = this ?: that

/**
 * Returns the current value if it is not null; otherwise, computes and returns the result of the [that] lambda function.
 *
 * @param that A lambda function that provides a fallback value if the current value is null.
 */
inline infix fun <T> T?.orElse(that: () -> T) = this ?: that()