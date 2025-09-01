package com.carthas.common.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.carthas.common.mvi.navigation.LocalNavigator
import com.carthas.common.mvi.navigation.Navigator
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatformTools
import kotlin.reflect.KClass


/**
 * Abstract base class representing a screen in a Compose Multiplatform application's navigation system.
 *
 * Extend this class to define custom screens by implementing the [CarthasContent] function to specify the screen's UI.
 *
 * Most [Screen]s should be `data object`s.
 */
abstract class Screen<S : UIState, I : UIIntent, E : UIEvent>(
    initialState: S,
) {
    /**
     * Represents the Koin dependency injection scope associated with this [Screen].
     *
     * The [scope] creates a unique DI context for each screen instance. It enables scoped dependencies tied to the
     * lifecycle of the [Screen].
     *
     * The lifetime of the [scope] should be explicitly controlled, where appropriate, by invoking `dispose()`
     * to release resources and clean up all objects managed by this [scope].
     */
    private val scope by lazy {
        KoinPlatformTools
            .defaultContext()
            .get()
            .createScope(
                scopeId = getScopeId(),
                qualifier = getScopeName(),
            )
    }

    /**
     * A [MutableStateFlow] holding the current UI state of the [Screen].
     */
    internal val stateFlow = MutableStateFlow(initialState)

    /**
     * Releases the current scope associated with the [Screen]. Only called if the implementing class also implements [Disposable].
     */
    internal fun dispose() = scope.close()

    /**
     * Defines the UI content of a [Screen].
     *
     * This abstract composable function should be overridden to implement the specific user interface
     * for the corresponding [Screen]. It is invoked by navigation utilities, such as [Navigator.CurrentScreen],
     * to render the content of the active screen in the navigation stack.
     *
     * If you are using Koin DI, and the [Screen] has a corresponding [CarthasViewModel], you should use the inline [Content]
     * helper function for a clean implementation.
     */
    @Composable
    abstract fun Content()

    /**
     * Defines a composable function to bind a [CarthasViewModel] to the UI represented by [content].
     *
     * @param VM The type of [CarthasViewModel] managing this UI's state, intents, and events.
     * @param vmClass The [KClass] of the [CarthasViewModel] used to instantiate or retrieve the appropriate vm instance.
     * @param viewModelParams Optional parameters required for the instantiation of the vm.
     * @param content A composable function representing the UI content, which takes the following parameters:
     * - `state`: The current UI state of type [S] contained within this screen and updated by the VM.
     * - `emitIntent`: A function to send user intents of type [I] to the ViewModel.
     * - `collectEvents`: A composable function to observe and handle events of type [E] emitted by the ViewModel.
     */
    @Composable
    fun <VM : CarthasViewModel<S, I, E>> CarthasContent(
        vmClass: KClass<VM>,
        vararg viewModelParams: Any,
        content: @Composable (state: S, emitIntent: (I) -> Unit, collectEvents: @Composable (FlowCollector<E>) -> Unit) -> Unit,
    ) {
        val navigator = LocalNavigator.current
        val uiState: S by stateFlow.collectAsState()

        // reuse vm instance while same Screen instance
        val viewModel: VM = remember(this.scope) {
            this.scope.get<VM>(
                clazz = vmClass,
                parameters = {
                    parametersOf(
                        this,  // for CarthasViewModel constructor
                        navigator,
                        *viewModelParams,
                    )
                },
            )
        }

        val coroutineScope = rememberCoroutineScope()

        val emitIntent: (I) -> Unit = remember(viewModel, coroutineScope) {
            { intent ->
                coroutineScope.launch { viewModel.intentFlow.emit(intent) }
            }
        }

        // when called, collect events while in composition
        val collectEvents: @Composable (FlowCollector<E>) -> Unit = remember(viewModel, coroutineScope) {
            { outerCollector ->
                LaunchedEffect(Unit) {
                    viewModel.eventFlow.collect { event ->
                        // launch a coroutine for each event emitted so that delays don't block
                        coroutineScope.launch { outerCollector.emit(event) }
                    }
                }
            }
        }

        content(uiState, emitIntent, collectEvents)
    }
}


/**
 * A no-op implementation of [Screen] with no state, intent, or event handling.
 *
 * This screen serves as a placeholder or no-op screen that does not manage any
 * specific UI state ([NoState]), user intents ([NoIntent]), or UI events ([NoEvent]).
 */
object NoScreen : Screen<NoState, NoIntent, NoEvent>(NoState) {
    @Composable
    override fun Content() { }
}