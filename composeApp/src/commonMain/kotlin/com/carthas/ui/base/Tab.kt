package com.carthas.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

interface Tab {
    val title: String
    val iconPainter: Painter
        @Composable get
}