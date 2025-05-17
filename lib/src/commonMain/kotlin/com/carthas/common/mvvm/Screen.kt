package com.carthas.common.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.carthas.common.mvvm.navigation.LocalNavigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * An abstract representation of a screen within the application. This class provides a framework for defining
 * the UI and state management logic for a screen, including integration with ViewModel-based architecture.
 *
 * Screens extending this class are expected to provide their own UI composition by implementing the [Content] method.
 */
abstract class Screen {
    /**
     * A composable function that connects a [CarthasViewModel] with a [UIState] to a screen's composable content.
     *
     * @param UIS The type of the UI state being managed by the ViewModel, which must implement the [UIState] interface.
     * @param VM The type of the ViewModel, which must be a subtype of [CarthasViewModel] and manage the specified UI state ([UIS]).
     * @param viewModelParams Parameters to be passed when instantiating the ViewModel through the Koin DI framework.
     * @param content The content to be rendered, which is a composable function receiving the current UI state ([UIS]) and the ViewModel ([VM]) as arguments.
     */
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

    /**
     * Represents the user interface of a [Screen]. This function is the entrypoint to the screen's composition.
     *
     * The implementation of this method should declare the UI layout and behavior for the
     * specific screen that subclasses [Screen]. It is called automatically to render the screen's
     * content wherever it is invoked in the navigation flow.
     *
     * This is an abstract method, and it must be implemented by subclasses of [Screen] to
     * provide their specific UI logic.
     */
    @Composable
    abstract fun Content()
}