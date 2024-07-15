@file:OptIn(ExperimentalMaterial3Api::class)

package com.smarttoolfactory.tutorial1_1basics.chapter3_layout

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.material3.BasicTooltipState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout


@Composable
fun rememberTooltipState(
    alignment: Alignment = Alignment.TopCenter,
    offset: IntOffset = IntOffset.Zero,
    initialIsVisible: Boolean = false,
    isPersistent: Boolean = true,
    mutatorMutex: MutatorMutex = BasicTooltipDefaults.GlobalMutatorMutex
) {
    remember(
        isPersistent,
        mutatorMutex,
    ) {
        TooltipStateImpl(
            alignment = alignment,
            offset = offset,
            initialIsVisible = initialIsVisible,
            isPersistent = isPersistent,
            mutatorMutex = mutatorMutex
        )
    }
}

@Stable
private class TooltipStateImpl(
    val alignment: Alignment,
    offset: IntOffset,
    initialIsVisible: Boolean,
    override val isPersistent: Boolean,
    private val mutatorMutex: MutatorMutex
) : TooltipState {
    override val transition: MutableTransitionState<Boolean> =
        MutableTransitionState(initialIsVisible)

    override val isVisible: Boolean
        get() = transition.currentState || transition.targetState

    /**
     * continuation used to clean up
     */
    private var job: (CancellableContinuation<Unit>)? = null


    var popupAlignment by mutableStateOf(alignment)
        internal set
    var offset by mutableStateOf(offset)
    var contentRect by mutableStateOf(IntRect.Zero)
        internal set
    var windowSize by mutableStateOf(IntSize.Zero)
        internal set
    var statusBarHeight: Float = 0f
        internal set

    /**
     * Show the tooltip associated with the current [BasicTooltipState].
     * When this method is called, all of the other tooltips associated
     * with [mutatorMutex] will be dismissed.
     *
     * @param mutatePriority [MutatePriority] to be used with [mutatorMutex].
     */
    override suspend fun show(
        mutatePriority: MutatePriority
    ) {
        val cancellableShow: suspend () -> Unit = {
            suspendCancellableCoroutine { continuation ->
                transition.targetState = true
                job = continuation
            }
        }

        // Show associated tooltip for [TooltipDuration] amount of time
        // or until tooltip is explicitly dismissed depending on [isPersistent].
        mutatorMutex.mutate(mutatePriority) {
            try {
                if (isPersistent) {
                    cancellableShow()
                } else {
                    withTimeout(BasicTooltipDefaults.TooltipDuration) {
                        cancellableShow()
                    }
                }
            } finally {
                // timeout or cancellation has occurred
                // and we close out the current tooltip.
                dismiss()
            }
        }
    }

    /**
     * Dismiss the tooltip associated with
     * this [TooltipState] if it's currently being shown.
     */
    override fun dismiss() {
        transition.targetState = false
    }

    /**
     * Cleans up [mutatorMutex] when the tooltip associated
     * with this state leaves Composition.
     */
    override fun onDispose() {
        job?.cancel()
    }
}


/**
 * BasicTooltip defaults that contain default values for tooltips created.
 */
internal object BasicTooltipDefaults {
    /**
     * The global/default [MutatorMutex] used to sync Tooltips.
     */
    val GlobalMutatorMutex: MutatorMutex = MutatorMutex()

    /**
     * The default duration, in milliseconds, that non-persistent tooltips
     * will show on the screen before dismissing.
     */
    const val TooltipDuration = 1500L
}
