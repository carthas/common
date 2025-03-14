package com.carthas.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf


val LocalNavigator: ProvidableCompositionLocal<Navigator> =
    staticCompositionLocalOf { error("no navigator provided") }

interface Navigator {
    var currentStack: List<Screen>

    infix fun push(screen: Screen) {
        currentStack = currentStack + screen
    }

    infix fun replace(screen: Screen) {
        pop()
        push(screen)
    }

    infix fun replaceAll(screen: Screen) {
        currentStack = listOf(screen)
    }

    fun pop() {
        if (currentStack.isNotEmpty()) {
            currentStack = currentStack - currentStack.last()
        }
    }

    infix fun popUntil(predicate: (Screen) -> Boolean) {
        while (currentStack.isNotEmpty() && !predicate(currentStack.last())) {
            pop()
        }
    }

    @Composable
    fun CurrentScreen() {
        currentStack.last().Content()
    }
}

class LinearNavigator(
    initialScreen: Screen,
) : Navigator {
    private var navigationStack: List<Screen> by mutableStateOf(
        listOf(initialScreen),
    )
    override var currentStack: List<Screen>
        get() = navigationStack
        set(value) { navigationStack = value }
}

class TabNavigator(
    initialTab: Tab,
    initialNavStacks: Map<Tab, List<Screen>>,
) : Navigator {
    private var currentTab: Tab by mutableStateOf(initialTab)

    private var navigationStacks by mutableStateOf(initialNavStacks)

    override var currentStack: List<Screen>
        get() = navigationStacks[currentTab]!!
        set(value) {
            navigationStacks = navigationStacks + (currentTab to value)
        }

    infix fun swapTo(tab: Tab) {
        currentTab = tab
    }

    infix fun isOn(tab: Tab): Boolean {
        return currentTab == tab
    }
}



