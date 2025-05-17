package com.carthas.common.mvvm.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.carthas.common.mvvm.Screen

/**
 * A CompositionLocal that provides access to a [Navigator] instance within a Compose hierarchy.
 *
 * The [LocalNavigator] can be used to perform navigation actions such as pushing, replacing,
 * or popping screens through the navigator's current navigation stack. The value of this composition
 * local must be provided higher up in the composition tree, typically using a concrete implementation
 * of the [Navigator] interface, such as [LinearNavigator] or [TabNavigator].
 *
 * If no [Navigator] is provided in the current composition context, accessing [LocalNavigator] will
 * throw an error.
 *
 * Designed to facilitate navigation in a fully object-oriented manner by sharing a navigator instance
 * across a composition.
 */
val LocalNavigator: ProvidableCompositionLocal<Navigator> =
    staticCompositionLocalOf { error("no navigator provided") }

/**
 * Navigator defines an interface for managing the navigation stack in an application.
 * It provides methods to manipulate and interact with the navigation stack, including
 * adding, removing, and replacing screens. The navigation stack is represented as a
 * mutable list of [Screen] objects, where the last screen in the stack is the currently
 * active screen.
 */
interface Navigator {
    /**
     * Represents the current navigation stack in the application. This stack is a list of [Screen] instances,
     * where each instance corresponds to a screen within the navigation flow. The stack's state determines
     * the order of screens—ensuring that the last screen in the list is the one currently displayed to the user.
     *
     * Operations such as pushing, replacing, or popping screens dynamically modify this stack, enabling
     * navigation behavior within the application. Changes to the stack reflect updates to the UI, as it plays
     * an integral role in determining the content rendered on the screen.
     *
     * Implementations of the [Navigator] interface define how this stack is managed, with variations
     * such as linear navigation or navigation across tabs. The stack updates are handled reactively,
     * supporting Compose's recomposition mechanism to reflect changes promptly.
     */
    var currentStack: List<Screen>

    /**
     * Adds the specified [Screen] to the end of the current navigation stack, making it the active screen.
     *
     * @param screen The [Screen] to be added to the current navigation stack.
     */
    infix fun push(screen: Screen) {
        currentStack = currentStack + screen
    }

    /**
     * Replaces the current top screen on the navigation stack with the specified [Screen].
     *
     * @param screen The [Screen] instance to be placed on top of the navigation stack,
     * effectively replacing the current top screen.
     */
    infix fun replace(screen: Screen) {
        pop()
        push(screen)
    }

    /**
     * Replaces the current navigation stack with a single [Screen], effectively resetting the stack
     * and setting the provided screen as the sole element in the navigation history.
     *
     * @param screen The screen to replace the current navigation stack with. This screen becomes
     * the only screen in the stack.
     */
    infix fun replaceAll(screen: Screen) {
        currentStack = listOf(screen)
    }

    /**
     * Removes the last screen from the current navigation stack, effectively popping the
     * topmost screen off the navigation history.
     *
     * This function is typically used to navigate back to the previous screen in
     * a sequential or linear flow.
     */
    fun pop() {
        if (currentStack.isNotEmpty()) {
            currentStack = currentStack - currentStack.last()
        }
    }

    /**
     * Removes screens from the navigation stack until a screen matches the given predicate.
     *
     * @param predicate A function that takes a [Screen] and returns a [Boolean]. If the predicate returns true
     * for the topmost screen in the stack, the popping stops. Otherwise, the screen is removed, and the evaluation continues.
     */
    infix fun popUntil(predicate: (Screen) -> Boolean) {
        while (currentStack.isNotEmpty() && !predicate(currentStack.last())) {
            pop()
        }
    }

    /**
     * An entrypoint Composable function that renders the content of the currently displayed screen.
     *
     * The current screen is determined as the screen at the top of the [currentStack] maintained by the [Navigator].
     * This method invokes the [Screen.Content] function of the current screen to display its user interface.
     * It ensures that screen composition dynamically reflects the navigation state managed by the [Navigator].
     */
    @Composable
    fun CurrentScreen() {
        currentStack.last().Content()
    }
}