package com.carthas.common.ext

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline.Generic
import androidx.compose.ui.graphics.Outline.Rectangle
import androidx.compose.ui.graphics.Outline.Rounded
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carthas.common.ext.Helpers.toPath


inline fun Modifier.modifyIf(condition: Boolean, block: Modifier.() -> Modifier) =
    if (condition) block()
    else this

inline fun <T> Modifier.modifyIfNotNull(nullable: T?, block: Modifier.(T) -> Modifier) =
    if (nullable.isNotNull()) block(nullable)
    else this

inline fun <reified T> Modifier.modifyIfIs(instance: Any, block: Modifier.(T) -> Modifier) =
    if (instance is T) block(instance)
    else this

fun Modifier.layeredShadow(
    elevation: Dp,
    shape: Shape,
    color: Color = Color.Black,
    offsetX: Dp = 0.dp,
    offsetY: Dp = elevation * 0.5f,
    layers: Int = 3,
) = drawBehind {
    if (elevation.value > 0) {
        val path = shape.toPath()

        for (layer in 1..layers) {
            val layerOffset = layer * (offsetY.toPx() / layers)
            val layerAlpha = color.alpha / layer

            drawPath(
                path = path,
                color = color,
                alpha = layerAlpha,
                style = Stroke(width = layerOffset * 2),
            )

            if (layer == 1) {
                translate(
                    left = offsetX.toPx(),
                    top = layerOffset,
                ) {
                    drawPath(
                        path = path,
                        color = color,
                        alpha = layerAlpha,
                    )
                }
            }
        }
    }
}

private object Helpers {

    context(drawScope: DrawScope)
    fun Shape.toPath(): Path {
        val outline = createOutline(
            size = drawScope.size,
            layoutDirection = drawScope.layoutDirection,
            density = drawScope,
        )

        return when (outline) {
            is Rectangle -> Path().apply {
                addRect(outline.rect)
            }

            is Rounded -> Path().apply {
                addRoundRect(outline.roundRect)
            }

            is Generic -> outline.path
        }
    }
}