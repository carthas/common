package com.carthas.common.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carthas.common.ext.casted
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


internal expect val ioBoundDispatcher: CoroutineDispatcher

internal expect val cpuBoundDispatcher: CoroutineDispatcher

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
) : ViewModel() {

    /**
     * A [MutableSharedFlow] used to receive [UIIntent] objects sent from the UI.
     */
    private val intentFlow = MutableSharedFlow<I>()

    /**
     * A [MutableSharedFlow] used to send [UIEvent] objects to the UI.
     */
    private val eventFlow = MutableSharedFlow<E>()

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
     * Internally received the given [intent] to [intentFlow]. Automatically handles the boilerplate of launching a coroutine
     * on the main thread within the [viewModelScope].
     */
    internal fun receiveIntent(intent: I) = launch { intentFlow.emit(intent) }

    /**
     * Allows implementations to emit event to [eventFlow]. Automatically handles the boilerplate of launching a coroutine
     * on the main thread within the [viewModelScope].
     */
    protected fun emitEvent(event: E) = launch { eventFlow.emit(event) }

    /**
     * Collects [UIEvent] objects from [eventFlow].
     */
    internal suspend fun collectEvents(collector: FlowCollector<E>): Nothing = eventFlow.collect(collector)

    /**
     * Updates the state of the ui state using a mutation function applied to the current state.
     *
     * @param SubState A subtype of the current UI state [S].
     * @param mutation A function that takes the current state of type [SubState] and returns a new state of type [S].
     */
    fun <SubState : S> mutateState(mutation: (SubState) -> S) = mutationFunction { mutation(it.casted()) }

    /**
     * Executes IO-bound work within the context of [ioBoundDispatcher], in this [viewModelScope].
     *
     * @param T The type of the result returned by [lambda].
     * @param lambda A suspending lambda function representing the IO-bound operation to be executed.
     * @return The result of the operation defined in [lambda].
     */
    suspend fun <T> ioBound(lambda: suspend CoroutineScope.() -> T): T =
        withContext(context = ioBoundDispatcher) { viewModelScope.lambda() }

    /**
     * Executes CPU-bound work within the context of [cpuBoundDispatcher], in this [viewModelScope].
     *
     * @param T The return type of the operation performed by [lambda].
     * @param lambda A suspending function block to be executed on the CPU-bound dispatcher.
     * @return The result of the operation defined in [lambda].
     */
    suspend fun <T> cpuBound(lambda: suspend CoroutineScope.() -> T) =
        withContext(context = cpuBoundDispatcher) { viewModelScope.lambda() }

    /**
     * Launches a coroutine on the main thread, in the [viewModelScope] associated with this ViewModel.
     *
     * @param lambda A suspend function block to be executed within the [CoroutineScope] of the ViewModel.
     */
    fun launch(lambda: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(block = lambda)

    /**
     * Launches a coroutine on the main thread, in the [viewModelScope] and returns a [kotlinx.coroutines.Deferred]
     * object that represents a future result of the operation.
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
object NoViewModel : CarthasViewModel<NoState, NoIntent, NoEvent>(NoScreen) {
    override suspend fun receive(intent: NoIntent) = Unit
}