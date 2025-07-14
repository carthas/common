package com.carthas.common.mvi.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.carthas.common.mvi.Screen


/**
 * A LinearNavigator is an implementation of the [Navigator] interface that manages a linear navigation stack.
 * This navigator maintains a sequential progression of [Screen] objects.
 *
 * @property initialScreen The initial [Screen] to be added to the navigation stack when the LinearNavigator
 * instance is created. This screen will be the first and active screen by default.
 */
class LinearNavigator(
    initialScreen: Screen<*,*,*>,
) : Navigator {
    /**
     * Represents the current navigation stack for the active navigation context, managed as a state
     * within the [LinearNavigator]. The stack maintains an ordered list of [Screen] objects,
     * where the last element represents the top of the stack and the screen currently displayed to the user.
     */
    private var navigationStack: List<Screen<*,*,*>> by mutableStateOf(
        listOf(initialScreen),
    )
    /**
     * An implementation that links [navigationStack] directly to [Navigator]'s [currentStack].
     */
    override var currentStack: List<Screen<*,*,*>>
        get() = navigationStack
        set(value) { navigationStack = value }
}

