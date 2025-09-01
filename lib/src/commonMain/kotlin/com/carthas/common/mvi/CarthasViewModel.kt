package com.carthas.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carthas.common.ext.casted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


private typealias StateMutation<S> = ((S) -> S) -> Unit

/**
 * An abstract base class for implementing a ViewModel in a Compose Multiplatform application's MVI architecture.
 *
 * [CarthasViewModel] manages interactions between the UI layer and the business logic, updating state ([UIState]),
 * receiving and reacting to intents ([UIIntent]), and sending one-off events ([UIEvent]) to the UI.
 *
 * This class leverages [viewModelScope] for coroutine management and includes dsl functions for IO-bound
 * or CPU-bound task execution.
 *
 * @param S Represents the type of UI state ([UIState]) associated with the ViewModel.
 * @param I Represents the type of UI intents ([UIIntent]) that can be received from the user interface.
 * @param E Represents the type of UI events ([UIEvent]) that can be emitted to the user interface.
 * @param screen The [Screen] instance associated with this ViewModel, which exposes a state mutation function.
 * @param mutationFunction A lambda to mutate and update the current state.
 */
abstract class CarthasViewModel<S : UIState, I : UIIntent, E : UIEvent>(
    screen: Screen<S, I, E>,
    private val mutationFunction: StateMutation<S> = screen::mutateState,
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext

    /**
     * A [MutableSharedFlow] used to receive [UIIntent] objects sent from the UI.
     */
    internal val intentFlow = MutableSharedFlow<I>()

    /**
     * A [MutableSharedFlow] used to send [UIEvent] objects to the UI.
     */
    internal val eventFlow = MutableSharedFlow<E>()

    init {
        // collect intents while this instance is alive
        launch {
            intentFlow.collect {
                receive(intent = it)
            }
        }
    }

    /**
     * Handles the provided [intent] to trigger business logic operations or state updates within the ViewModel.
     *
     * @param intent The [UIIntent] instance representing an action or event to be processed.
     */
    abstract suspend fun receive(intent: I)

    /**
     * Updates the state of the ui state using a mutation function applied to the current state.
     *
     * @param SubState A subtype of the current UI state [S].
     * @param mutation A function that takes the current state of type [SubState] and returns a new state of type [S].
     */
    fun <SubState : S> mutateState(mutation: (SubState) -> S) = mutationFunction { mutation(it.casted()) }
}

/**
 * A no-op implementation of [CarthasViewModel] with [NoState] and [NoIntent].
 *
 * This object is used as a placeholder ViewModel when no specific state or intent handling is required.
 * It always returns [Unit] for any intent received.
 */
object NoViewModel : CarthasViewModel<NoState, NoIntent, NoEvent>(NoScreen) {
    override suspend fun receive(intent: NoIntent) = Unit
}