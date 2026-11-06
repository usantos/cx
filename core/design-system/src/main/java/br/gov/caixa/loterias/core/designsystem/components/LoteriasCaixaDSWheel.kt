package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/** Wheel Compose do LoteriasCaixaDS com seleção central e encaixe ao terminar o gesto. */
@Composable
fun LoteriasCaixaDSWheel(
    options: List<String>,
    selectedIndex: Int,
    onSelectedIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 32.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    color: androidx.compose.ui.graphics.Color = LoteriasCaixaDSColorTokens.TutorialGreenButton,
    fontFamily: FontFamily = LoteriasCaixaDSFontFamily,
    wrap: Boolean = false,
    accessibilityLabel: String = stringResource(R.string.accessibility_selection),
) {
    if (options.isEmpty()) return
    val scaledItemHeight = itemHeight * LocalDensity.current.fontScale.coerceAtLeast(1f)
    val selected = selectedIndex.coerceIn(options.indices)
    val virtualCount = if (wrap) options.size * WHEEL_VIRTUAL_CYCLE_COUNT else options.size
    val initialIndex = if (wrap) options.size * WHEEL_CENTRAL_CYCLE + selected else selected
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
                LoteriasCaixaDSWheelOption(
                    label = options[optionIndex],
                    selected = optionIndex == selected,
                    centralCycle = !wrap || virtualIndex / options.size == WHEEL_CENTRAL_CYCLE,
                    itemHeight = scaledItemHeight,
                    fontSize = fontSize,
                    color = color,
                    fontFamily = fontFamily,
                    onClick = {
                        if (optionIndex == selected) {
                            onSelectedIndex(optionIndex)
                        } else {
                            scope.launch { listState.animateScrollToItem(virtualIndex) }
                        }
                    }
                )
            }
        }
    }
}

private const val WHEEL_VIRTUAL_CYCLE_COUNT = 101
private const val WHEEL_CENTRAL_CYCLE = 50
