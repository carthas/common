package com.carthas.common.mvvm.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.carthas.common.mvvm.Screen


/**
 * A LinearNavigator is an implementation of the [Navigator] interface that manages a linear navigation stack.
 * This stack-based navigator maintains a sequential progression of [Screen] objects.
 *
 * @property initialScreen The initial [Screen] to be added to the navigation stack when the LinearNavigator
 * instance is created. This screen will be the first and active screen by default.
 */
class LinearNavigator(
    initialScreen: Screen,
) : Navigator {
    /**
     * Represents the current navigation stack for the active navigation context, managed as a state
     * within the [LinearNavigator]. The stack maintains an ordered list of [Screen] objects,
     * where the last element represents the top of the stack and the screen currently displayed to the user.
     *
     * This property uses Compose's [mutableStateOf] to ensure that changes to the navigation stack
     * trigger recomposition of the UI. The stack is initialized with the provided [initialScreen],
     * and subsequent navigation operations (e.g., push, pop) dynamically modify its state.
     *
     * Modifications to this stack are reflected in the [currentStack] property of the enclosing
     * navigator class, enabling seamless state synchronization and navigation control.
     */
    private var navigationStack: List<Screen> by mutableStateOf(
        listOf(initialScreen),
    )
    /**
     * An implementation that links [navigationStack] directly to [Navigator]'s [currentStack].
     */
    override var currentStack: List<Screen>
        get() = navigationStack
        set(value) { navigationStack = value }
}

