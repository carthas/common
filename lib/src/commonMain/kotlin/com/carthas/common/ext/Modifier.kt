package com.carthas.common.ext

import androidx.compose.ui.Modifier


/**
 * Modifies the current [Modifier] if the condition is met.
 *
 * @param condition A [Boolean] determining whether the modification should be applied.
 * @param block A lambda function that defines the modification to apply if [condition] is true.
 * @return The modified [Modifier] if [condition] is true; otherwise, returns the current [Modifier].
 */
inline fun Modifier.modifyIf(condition: Boolean, block: Modifier.() -> Modifier) =
    if (condition) block()
    else this

/**
 * Applies the given [block] function to this [Modifier] if the [nullable] value is not null.
 *
 * @param nullable The nullable value to check before applying the [block].
 * @param block A function that modifies the [Modifier] when [nullable] is not null.
 * @return The modified [Modifier] if [nullable] is not null, or the original [Modifier] otherwise.
 */
inline fun <T> Modifier.modifyIfNotNull(nullable: T?, block: Modifier.(T) -> Modifier) =
    if (nullable.isNotNull()) block(nullable)
    else this

/**
 * Modifies the [Modifier] if the specified [instance] is of type [T].
 *
 * @param T The type that [instance] is checked against.
 * @param instance The object to check for type [T].
 * @param block A lambda function defining modifications to apply to the modifier if [instance] is of type [T].
 * @return The modified [Modifier] if [instance] is of type [T], or the original [Modifier] if not.
 */
inline fun <reified T> Modifier.modifyIfIs(instance: Any, block: Modifier.(T) -> Modifier) =
    if (instance is T) block(instance)
    else this
