package com.carthas.common.mvi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Represents a tab, used in tabbed interfaces.
 *
 * Each tab has a title and an associated icon. The tab's properties can be displayed
 * in a user interface to represent its corresponding navigation destination.
 */
interface Tab {
    /**
     * Represents the title of the tab.
     *
     * The title is a textual label that describes the purpose or content of the tab.
     * It is displayed in the user interface to help users identify and navigate between
     * different tabs within the application.
     */
    val title: String
    /**
     * Provides a [Painter] representing the icon of a tab.
     * This property is designed to be used within a @Composable context,
     * enabling dynamic composition of tab icons in a UI.
     */
    val iconPainter: Painter
        @Composable get
}