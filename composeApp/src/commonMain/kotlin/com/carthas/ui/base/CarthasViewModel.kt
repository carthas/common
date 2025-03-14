package com.carthas.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class CarthasViewModel<StateType : UIState>(initialState: StateType) : ViewModel() {
    private val uiStateFlow = MutableStateFlow(initialState)
    private val uiState: StateType
        get() = uiStateFlow.value

    fun <SubState : StateType> mutateState(mutation: (SubState) -> StateType) = uiStateFlow.update {
        @Suppress("UNCHECKED_CAST")
        mutation(it as SubState)
    }
    @Composable
    fun collectState() = uiStateFlow.collectAsState()
    fun launch(lambda: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(block = lambda)
    fun <SubState : StateType> launchWithState(lambda: suspend CoroutineScope.(SubState) -> Unit) =
        launch {
            @Suppress("UNCHECKED_CAST")
            lambda(uiState as SubState)
        }
}

object NoViewModel : CarthasViewModel<NoUIState>(NoUIState)