package com.carthas.common.mvi.event

object ScreenEventDispatcher {
    private var currentScreen: Screen? = null

    fun setActiveScreen(screen: Screen) {
        currentScreen = screen
    }

    fun sendUiEvent(event: UIEvent) {
        currentScreen?.onUiEvent(event)
    }
}