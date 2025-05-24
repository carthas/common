package com.carthas.common.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * Base class for managing UI state ([UIState]) and handling user intents ([UIIntent]) in an MVI architecture.
 *
 * @param S Type of the [UIState] to be managed.
 * @param I Type of the [UIIntent] to be handled.
 * @param initialState Initial state of type [UIState].
 */
abstract class CarthasViewModel<S : UIState, in I : UIIntent>(
    initialState: S,
) : ViewModel() {
    /**
     * A [MutableStateFlow] holding the current UI state of the [Screen] this VM manages.
     *
     * Used to manage and propagate state updates efficiently across components, ensuring
     * reactive state management in a Compose Multiplatform context.
     */
    private val stateFlow = MutableStateFlow(initialState)

    /**
     * Casts the current state stored in [stateFlow] to the specified subtype [SubState].
     *
     * @return The current state casted to [SubState].
     * @throws ClassCastException if the cast is invalid.
     */
    @Suppress("UNCHECKED_CAST")
    private fun <SubState : S> getCastedState(): SubState = stateFlow.value as SubState

    /**
     * Handles the provided [intent] to trigger business logic operations or state updates within the ViewModel.
     *
     * @param intent The [UIIntent] instance representing an action or event to be processed.
     */
    abstract fun receive(intent: I)

    /**
     * Collects the current [UIState] from the internal [stateFlow] and observes updates to it in a composable scope.
     *
     * Designed for use in composable functions to provide a reactive [State] object that reflects
     * the latest state from the associated [CarthasViewModel].
     *
     * This function enables state-driven recomposition, ensuring the UI automatically updates when the state changes.
     *
     * @return A [State] object of type [S], representing the current UI state.
     */
    @Composable
    fun collectState() = stateFlow.collectAsState()

    /**
     * Acquires the current state as [SubState] type and uses it, returning the result of [lambda].
     *
     * @param lambda A function that receives the state ([SubState]) and returns some value of type [R].
     * @return The result of applying [lambda] to the current state.
     */
    fun <SubState : S, R> useState(lambda: (SubState) -> R): R = lambda(getCastedState())

    /**
     * Updates the state of the [stateFlow] using a mutation function applied to the current state.
     *
     * @param SubState A subtype of the current UI state [S].
     * @param mutation A function that takes the current state of type [SubState] and returns a new state of type [S].
     */
    fun <SubState : S> mutateState(mutation: (SubState) -> S) = stateFlow.update { mutation(getCastedState()) }

    /**
     * Launches a coroutine in the [viewModelScope] associated with this ViewModel.
     *
     * @param lambda A suspend function block to be executed within the [CoroutineScope] of the ViewModel.
     */
    fun launch(lambda: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(block = lambda)

    /**
     * Launches a coroutine in [viewModelScope] and returns a [kotlinx.coroutines.Deferred] object that represents
     * a future result of the operation.
     *
     * @param lambda A suspending lambda that defines the operation to be performed asynchronously.
     * @return A [kotlinx.coroutines.Deferred] object that can be used to retrieve the result of the executed task.
     */
    fun <T> async(lambda: suspend CoroutineScope.() -> T) = viewModelScope.async(block = lambda)
}

/**
 * A no-op implementation of [CarthasViewModel] with [NoState] and [NoIntent].
 *
 * This object is used as a placeholder ViewModel when no specific state or intent handling is required.
 * It always returns [Unit] for any intent received.
 */
object NoViewModel : CarthasViewModel<NoState, NoIntent>(NoState) {
    override fun receive(intent: NoIntent) = Unit
}