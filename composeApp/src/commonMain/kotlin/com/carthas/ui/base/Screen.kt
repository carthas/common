package com.carthas.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

abstract class Screen {
    @Composable
    inline fun <UIS : UIState, reified VM : CarthasViewModel<UIS>> content(
        vararg viewModelParams: Any,
        content: @Composable (UIS, VM) -> Unit,
    ) {
        val navigator = LocalNavigator.current
        val viewModel = koinViewModel<VM> { parametersOf(navigator, *viewModelParams) }
        val uiState by viewModel.collectState()
        content(uiState, viewModel)
    }

    @Composable
    abstract fun Content()
}