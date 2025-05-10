package com.carthas.common.mvvm.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.carthas.common.mvvm.Screen
import kotlin.collections.plus

/**
 * A class that manages navigation for an application with multiple tabs. Each tab maintains its own
 * navigation stack, enabling independent navigation within each tab while providing a shared navigation
 * state across all tabs.
 *
 * @param initialTab The default tab to display when the TabNavigator is initialized.
 * @param initialNavStacks A map of tabs to their respective navigation stacks. Each navigation stack
 * contains a list of [Screen] instances representing the screens in the navigation history for the tab.
 */
class TabNavigator(
    initialTab: Tab,
    initialNavStacks: Map<Tab, List<Screen>>,
) : Navigator {
    /**
     * Represents the currently active tab in the tab-based navigation system.
     *
     * This property is managed as a state using Compose's reactive state management, ensuring
     * that changes to the active tab trigger recomposition UI.
     */
    private var currentTab: Tab by mutableStateOf(initialTab)

    /**
     * Holds a mapping of navigation stacks for each tab in the application.
     *
     * Each entry in this map corresponds to a specific [Tab] and contains a list of [Screen] instances
     * representing the navigation stack for that tab.
     */
    private var navigationStacks by mutableStateOf(initialNavStacks)

    /**
     * Connects the currently active tab's navigation stack to the [Navigator] interface's [currentStack], so that
     * [Navigator] operations affect the [currentTab]'s navigation stack.
     */
    override var currentStack: List<Screen>
        get() = navigationStacks[currentTab]!!
        set(value) {
            navigationStacks = navigationStacks + (currentTab to value)
        }

    /**
     * Switches the currently active tab to the specified [Tab].
     *
     * @param tab The [Tab] to switch to as the current active tab.
     */
    infix fun swapTo(tab: Tab) {
        currentTab = tab
    }

    /**
     * Checks if the specified tab is currently active.
     *
     * @param tab The [Tab] to check against the currently active tab.
     * @return `true` if the specified tab is the currently active tab, `false` otherwise.
     */
    infix fun isOn(tab: Tab): Boolean {
        return currentTab == tab
    }
}