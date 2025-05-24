package com.carthas.common.mvi

import androidx.compose.runtime.Immutable

/**
 * Represents the state of the user interface in an MVI architecture.
 *
 * Classes or objects implementing this interface define a snapshot of the UI's data
 * at a given point in time. These states are immutable and are managed by the ViewModel ([CarthasViewModel]).
 *
 * Implementations should be their own sealed hierarchy.
 */
@Immutable
interface UIState

/**
 * Represents an empty or no-op implementation of [UIState].
 *
 * Used as a placeholder or default state in scenarios where no specific state is required.
 * [NoState] is beneficial when your VM has no state to manage.
 */
@Immutable
object NoState : UIState