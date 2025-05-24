package com.carthas.common.mvi


/**
 * Represents a user or UI-triggered action in an MVI architecture.
 *
 * Classes or objects implementing this interface define the intents that a user interface can dispatch
 * to a corresponding ViewModel for processing.
 *
 * [UIIntent] is typically used to handle events such as user interactions, navigation triggers, or
 * other actions that might lead to changes in the UI state ([UIState]).
 *
 * Implementations should be their own sealed hierarchy.
 */
interface UIIntent

/**
 * Represents a no-op implementation of [UIIntent].
 *
 * This object is used as a placeholder or default intent when no specific
 * actions or user inputs need to be handled.
 */
object NoIntent : UIIntent