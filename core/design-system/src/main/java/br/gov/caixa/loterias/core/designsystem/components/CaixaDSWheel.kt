package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/** Wheel Compose do CaixaDS com seleção central e encaixe ao terminar o gesto. */
@Composable
fun CaixaDSWheel(
    options: List<String>,
    selectedIndex: Int,
    onSelectedIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 32.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    color: androidx.compose.ui.graphics.Color = CaixaDSColorTokens.TutorialGreenButton,
    fontFamily: FontFamily = CaixaDSFontFamily,
    wrap: Boolean = false,
    accessibilityLabel: String = stringResource(R.string.accessibility_selection),
) {
    if (options.isEmpty()) return
    val scaledItemHeight = itemHeight * LocalDensity.current.fontScale.coerceAtLeast(1f)
    val selected = selectedIndex.coerceIn(options.indices)
    val virtualCount = if (wrap) options.size * 101 else options.size
    val initialIndex = if (wrap) options.size * 50 + selected else selected
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val scope = rememberCoroutineScope()
    val currentSelected by rememberUpdatedState(selected)
    val currentOnSelected by rememberUpdatedState(onSelectedIndex)

    LaunchedEffect(options, selected, wrap) { listState.scrollToItem(initialIndex) }
    LaunchedEffect(listState, options.size, wrap) {
        var userScrolled = false
        snapshotFlow { listState.isScrollInProgress }.distinctUntilChanged().collect { scrolling ->
            if (scrolling) {
                userScrolled = true
            } else if (userScrolled) {
                val center = (listState.layoutInfo.viewportStartOffset + listState.layoutInfo.viewportEndOffset) / 2
                val index = listState.layoutInfo.visibleItemsInfo
                    .minByOrNull { kotlin.math.abs(it.offset + it.size / 2 - center) }
                    ?.index ?: return@collect
                val logicalIndex = index % options.size
                if (logicalIndex != currentSelected) currentOnSelected(logicalIndex)
            }
        }
    }

    BoxWithConstraints(
        modifier.clipToBounds()
            .wheelAccessibility(options, selected, wrap, accessibilityLabel, onSelectedIndex)
    ) {
        val topPadding = ((maxHeight - scaledItemHeight) / 2).coerceAtLeast(0.dp)
        LazyColumn(
            state = listState,
            modifier = (Modifier.fillMaxSize()).uiId("ds.0057"),
            contentPadding = PaddingValues(vertical = topPadding),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            verticalArrangement = Arrangement.Top
        ) {
            items(virtualCount) { virtualIndex ->
                val optionIndex = virtualIndex % options.size
                // Apenas um ciclo lógico finito participa da navegação por acessibilidade/teclado.
                // Os demais ciclos mantêm o comportamento do ponteiro para a repetição visual do seletor.
                val centralCycle = !wrap || virtualIndex / options.size == 50
                val presentation = if (centralCycle) {
                    Modifier
                } else {
                    Modifier
                        .clearAndSetSemantics { }
                        .focusProperties { canFocus = false }
                }
                Box(
                    presentation.fillMaxWidth().height(scaledItemHeight)
                        .uiId("ds.0058").selectable(selected = optionIndex == selected, role = Role.RadioButton) {
                            if (optionIndex == selected) {
                                onSelectedIndex(optionIndex)
                            } else {
                                scope.launch { listState.animateScrollToItem(virtualIndex) }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        options[optionIndex],
                        color = color,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun Modifier.wheelAccessibility(
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
