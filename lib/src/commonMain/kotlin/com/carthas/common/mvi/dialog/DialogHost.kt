package com.carthas.common.mvi.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import com.carthas.common.ext.modifyIf


/**
 * Provides a host for managing and displaying dialogs using a [DialogManager].
 *
 * The [DialogHost] is responsible for rendering dialogs and managing their lifecycle in accordance with
 * the state and behavior defined in [DialogManager]. It applies animations, dismissal behavior, and overlays
 * as specified by individual [Dialog] properties.
 *
 * The composable [mainContent] is rendered first, followed by any active dialogs. It is wrapped in
 * a [CompositionLocalProvider] to make the [LocalDialogManager] accessible to the composable hierarchy.
 *
 * @param mainContent The primary composable content to be displayed underneath the dialog overlay. Dialogs managed
 * by [DialogManager] are rendered above this content.
 */
@Composable
fun DialogHost(
    mainContent: @Composable () -> Unit,
) {
    val dialogManager = remember { DialogManager() }

    CompositionLocalProvider(LocalDialogManager provides dialogManager) {

        // display the main content first
        mainContent()

        // display any active dialogs
        dialogManager.dialogStack.forEachIndexed { index, dialog ->
            val isTop = index == dialogManager.dialogStack.lastIndex
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                visible = true  // set visibility to true after the first composition to activate enter transition

                dialogManager.dismissEventFlow.collect {
                    // if this dialog has been dismissed, set visibility to false
                    if (it.dialogKey == dialog.key) visible = false
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = dialog.enterTransition,
                exit = dialog.exitTransition,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(index.toFloat()),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Center,
                ) {
                    // dims the background, receives taps and dismisses the dialog
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .modifyIf(isTop && dialog.dismissOnClickOutside) {
                                pointerInput(Unit) { detectTapGestures { dialogManager.dismiss() } }
                            },
                    )
                    // displays the dialog
                    Box(
                        modifier = Modifier.pointerInput(Unit) {},
                        contentAlignment = Center,
                        content = { dialog.content() },
                    )
                }
            }
        }
    }
}