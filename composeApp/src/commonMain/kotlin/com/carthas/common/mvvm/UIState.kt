package com.carthas.common.mvvm

import androidx.compose.runtime.Immutable

/**
 * Represents the state of a user interface in the MVVM architecture.
 *
 * Classes or objects implementing this interface are used as a type-safe definition
 * of the UI state managed by ViewModels, ensuring a consistent and explicit way
 * of handling states.
 *
 * The interface can be implemented to define either generic or specific UI states
 * depending on the requirements of the screen or component.
 */
@Immutable
interface UIState

/**
 * Represents a placeholder UI state that does not contain any data or behavior.
 *
 * This object implements the [UIState] interface and is used in scenarios where there is no
 * specific state required for a screen or component.
 */
@Immutable
object NoUIState : UIState