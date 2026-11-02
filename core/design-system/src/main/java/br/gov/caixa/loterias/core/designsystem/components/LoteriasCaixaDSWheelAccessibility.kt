package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import br.gov.caixa.loterias.core.designsystem.R

@Composable
internal fun Modifier.wheelAccessibility(
    options: List<String>,
    selected: Int,
    wrap: Boolean,
    accessibilityLabel: String,
    onSelect: (Int) -> Unit,
): Modifier {
    val previousLabel = stringResource(R.string.accessibility_previous_option)
    val nextLabel = stringResource(R.string.accessibility_next_option)
    fun selectIndex(index: Int): Boolean {
        if (index !in options.indices) return false
        if (index != selected) onSelect(index)
        return true
    }
    fun step(delta: Int): Boolean {
        val next = if (wrap) (selected + delta + options.size) % options.size else selected + delta
        return selectIndex(next)
    }
    return this.onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) {
            false
        } else {
            when (event.key) {
                Key.DirectionDown, Key.DirectionRight -> {
                    step(1)
                    true
                }

                Key.DirectionUp, Key.DirectionLeft -> {
                    step(-1)
                    true
                }

                else -> false
            }
        }
    }
        .semantics {
            contentDescription = accessibilityLabel
            stateDescription = options[selected]
            progressBarRangeInfo =
                ProgressBarRangeInfo(selected.toFloat(), 0f..options.lastIndex.toFloat(), (options.size - 2).coerceAtLeast(0))
            setProgress { requested ->
                if (requested.isFinite()) {
                    selectIndex(kotlin.math.round(requested).toInt().coerceIn(options.indices))
                } else {
                    false
                }
            }
            customActions = listOf(
                CustomAccessibilityAction(previousLabel) { step(-1) },
                CustomAccessibilityAction(nextLabel) { step(1) }
            )
        }.focusable()
}
