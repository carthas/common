package com.carthas.common.mvi


/**
 * Represents a UI event in an MVI architecture.
 *
 * Classes or objects implementing this interface define events emitted by the ViewModel ([CarthasViewModel])
 * to the UI. These events are typically one-time occurrences such as showing a toast, displaying a dialog,
 * or navigating to another screen.
 *
 * Unlike [UIState], which represents a continuous snapshot of the user interface, events are transient
 * and are meant to be consumed once, often for purposes that do not directly affect the UI state.
 *
 * Implementations should be their own sealed hierarchy.
 */
interface UIEvent

/**
 * Represents a no-op or placeholder event in an MVI architecture.
 *
 * [NoEvent] implements [UIEvent] as a default or empty event type, used in scenarios
 * where no specific events need to be emitted or handled.
 *
 * Useful when defining [Screen] implementations with no event-handling requirements.
 */
data object NoEvent : UIEvent