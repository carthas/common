package com.carthas.common.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.carthas.common.mvi.navigation.LocalNavigator
import com.carthas.common.mvi.navigation.Navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


/**
 * Abstract base class representing a screen in a Compose Multiplatform application's navigation system.
 *
 * Extend this class to define custom screens by implementing the [Content] function to specify the screen's UI.
 *
 * Most [Screen]s should be `data object`s.
 */
abstract class Screen {
    /**
     * Defines the UI content of a [Screen].
     *
     * This abstract composable function should be overridden to implement the specific user interface
     * for the corresponding [Screen]. It is invoked by navigation utilities, such as [Navigator.CurrentScreen],
     * to render the content of the active screen in the navigation stack.
     *
     * If you are using Koin DI, and the [Screen] has a corresponding [CarthasViewModel], you should use the [content]
     * function for a clean implementation.
     */
    @Composable
    abstract fun Content()

    /**
     * A composable function that binds a [CarthasViewModel] to UI content, providing the current [UIState]
     * and a dispatch function for handling [UIIntent]. The content's dispatch function is the [CarthasViewModel]'s
     * receive function, which allows the screen to dispatch events or actions to the VM.
     *
     * This function retrieves the ViewModel instance using koin dependency injection and collects its state,
     * enabling state-driven recomposition.
     *
     * *NOTE* you will need to provide a type param for VM at the call site for DI to work properly. e.g.:
     *
     * content<MyViewModel, MyState, MyIntent> { state, dispatch ->
     *     // composables
     * }
     *
     * or
     *
     * content<MyViewModel, _, _> { state, dispatch ->
     *     // composables
     * }
     *
     * @param viewModelParams Parameters passed to the ViewModel instance at dependency resolution time.
     * @param content A composable function that receives the UI state and dispatch function.
     */
    @Composable
    inline fun <reified VM : CarthasViewModel<S, I>, S : UIState, I : UIIntent> Content(
        vararg viewModelParams: Any,
        content: @Composable (state: S, dispatchFunction: (I) -> Unit) -> Unit,
    ) {
        val navigator: Navigator = LocalNavigator.current
        val viewModel: VM = koinViewModel { parametersOf(navigator, *viewModelParams) }
        val uiState: S by viewModel.collectState()

        content(uiState, viewModel::receive)
    }
}