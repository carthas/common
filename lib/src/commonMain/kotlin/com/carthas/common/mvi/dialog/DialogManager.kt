package com.carthas.common.mvi.dialog

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.carthas.common.mvi.ioBoundDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


/**
 * A [CompositionLocal] used to provide and retrieve a [DialogManager] instance within the Compose hierarchy.
 *
 * [DialogManager] manages the display, dismissal, and internal state of dialogs.
 * By default, the composition local throws an exception if accessed without a provided value.
 * It is typically set at a higher level in the compose tree using [CompositionLocalProvider].
 *
 * This composition local enables features like showing or dismissing dialogs through [DialogManager],
 * typically used in conjunction with a dialog host implementation.
 */
val LocalDialogManager = staticCompositionLocalOf<DialogManager> { error("No DialogManager provided") }

/**
 * Manages a stack of [Dialog]s to display in a UI component.
 * Provides functions for adding, dismissing, and clearing dialogs.
 */
class DialogManager internal constructor() : CoroutineScope {

    /**
     * Represents an event triggered when a dialog is dismissed.
     */
    internal data class DismissEvent(val dialogKey: String)

    /**
     * The [CoroutineContext] used to delay when dismissing animated dialogs.
     */
    override val coroutineContext: CoroutineContext = Dispatchers.Main

    /**
     * A private stack as a [androidx.compose.runtime.snapshots.SnapshotStateList] that holds the active [Dialog]
     * instances managed by [DialogManager].
     */
    private val _dialogStack = mutableStateListOf<Dialog>()

    internal val dialogStack: List<Dialog>
        get() = _dialogStack

    /**
     * A private [MutableSharedFlow] that emits [DismissEvent]s when a dialog is dismissed.
     */
    private val _dismissEventFlow = MutableSharedFlow<DismissEvent>()

    internal val dismissEventFlow: SharedFlow<DismissEvent>
        get() = _dismissEventFlow

    /**
     * Adds a new [Dialog] to the list of managed dialogs.
     */
    fun show(dialog: Dialog) {
        _dialogStack.add(dialog)
    }

    /**
     * Dismisses the dialog with the matching key, or the topmost dialog in the dialog stack if no key is provided.
     */
    fun dismiss(key: String = _dialogStack.last().key) {
        if (_dialogStack.isNotEmpty()) launch {
            _dismissEventFlow.emit(DismissEvent(dialogKey = key))
            delay(_dialogStack.last().dismissDelay)
            _dialogStack.removeAll { it.key == key }
        }
    }

    /**
     * Dismisses all dialogs managed by the [DialogManager].
     */
    fun dismissAll() = repeat(_dialogStack.size) { dismiss() }
}