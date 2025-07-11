package com.carthas.common.mvi.event

import androidx.compose.runtime.Immutable

/**
 * Represents a one-time UI effect sent to a screen.
 *
 * Unlike [UIIntent], which flows from the UI to the ViewModel, [UIEvent] flows directly
 * to the currently active [Screen] — often for transient actions such as focusing input,
 * showing dialogs, snackbars, navigation bars, etc.
 *
 * Screens can override [Screen.onUiEvent] to handle these events.
 */
interface UIEvent

data class ShowDialog(val message: String) : UIEvent
object DismissDialog : UIEvent