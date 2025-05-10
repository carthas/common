package com.carthas.common.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * A Compose [ViewModel] extension designed to manage a [Screen]'s UI state and expose a DSL to read and mutate it.
 *
 * @param StateType The type of the UI state being managed, which must implement the [UIState] interface.
 * @param initialState The initial state of the ViewModel.
 */
abstract class CarthasViewModel<StateType : UIState>(initialState: StateType) : ViewModel() {
    private val uiStateFlow = MutableStateFlow(initialState)

    /**
     * Updates the state of the ViewModel by applying a mutation function to the current state.
     *
     * @param SubState The type of state being mutated. Must be a subtype of [StateType].
     * @param mutation A function that receives the current state of type SubState and returns a new state of type [StateType].
     */
    fun <SubState : StateType> mutateState(mutation: (SubState) -> StateType) = uiStateFlow.update {
        @Suppress("UNCHECKED_CAST")
        mutation(it as SubState)
    }

    /**
     * Collects the current state from the internal UI state flow and provides it as a Compose [androidx.compose.runtime.State].
     * This function is designed to be used in composable functions to observe and react to state changes.
     *
     * @return A Compose [androidx.compose.runtime.State] object reflecting the current state of the [Screen], which can be
     * observed for state changes.
     */
    @Composable
    fun collectState() = uiStateFlow.collectAsState()

    /**
     * Launches a coroutine within the [viewModelScope], which is tied to the lifecycle of the [ViewModel].
     *
     * @param lambda A suspending lambda function that defines the tasks to be executed
     * in the coroutine.
     */
    fun launch(lambda: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(block = lambda)

    /**
     * Launches a coroutine within the [viewModelScope] and provides the current UI state as a parameter
     * to the given suspend function. This method is used to perform asynchronous work using the current state.
     *
     * @param SubState The subtype of the state being passed to the lambda. Must be a subtype of [StateType].
     * @param lambda A suspend function that receives the current UI state of type [SubState] and performs a coroutine operation.
     */
    fun <SubState : StateType> launchWithState(lambda: suspend CoroutineScope.(SubState) -> Unit) = launch {
        @Suppress("UNCHECKED_CAST")
        lambda(uiStateFlow.value as SubState)
    }
}

/**
 * A singleton implementation of the [CarthasViewModel] class using [NoUIState] as the UI state type.
 * This is a stateless ViewModel designed to represent screens or components that do not require
 * any state management.
 *
 * [NoUIState] is a predefined, immutable object implementing the [UIState] interface,
 * used in scenarios where no specific UI state is needed or a placeholder state is sufficient.
 */
object NoViewModel : CarthasViewModel<NoUIState>(NoUIState)