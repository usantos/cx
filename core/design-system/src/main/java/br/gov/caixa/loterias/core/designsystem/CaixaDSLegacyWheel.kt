package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/** Wheel Compose do CaixaDS com seleção central e encaixe ao terminar o gesto. */
@Composable
fun CaixaDSLegacyWheel(
    options: List<String>,
    selectedIndex: Int,
    onSelectedIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 32.dp,
    fontSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    color: androidx.compose.ui.graphics.Color = CaixaDSColorTokens.LegacyRapidWheel,
    fontFamily: FontFamily = CaixaDSFontFamily,
    wrap: Boolean = false,
) {
    if (options.isEmpty()) return
    val selected = selectedIndex.coerceIn(options.indices)
    val virtualCount = if (wrap) options.size * 101 else options.size
    val initialIndex = if (wrap) options.size * 50 + selected else selected
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val scope = rememberCoroutineScope()

    LaunchedEffect(options, selected, wrap) { listState.scrollToItem(initialIndex) }
    LaunchedEffect(listState, options.size, wrap) {
        var userScrolled = false
        snapshotFlow { listState.isScrollInProgress }.distinctUntilChanged().collect { scrolling ->
            if (scrolling) userScrolled = true
            else if (userScrolled) {
                val center = (listState.layoutInfo.viewportStartOffset + listState.layoutInfo.viewportEndOffset) / 2
                val index = listState.layoutInfo.visibleItemsInfo
                    .minByOrNull { kotlin.math.abs(it.offset + it.size / 2 - center) }
                    ?.index ?: return@collect
                onSelectedIndex(index % options.size)
            }
        }
    }

    BoxWithConstraints(modifier.clipToBounds()) {
        val topPadding = ((maxHeight - itemHeight) / 2).coerceAtLeast(0.dp)
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = topPadding),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            verticalArrangement = Arrangement.Top,
        ) {
            items(virtualCount) { virtualIndex ->
                val optionIndex = virtualIndex % options.size
                Box(
                    Modifier.fillMaxWidth().height(itemHeight)
                        .clickable { scope.launch { listState.animateScrollToItem(virtualIndex) } },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        options[optionIndex],
                        color = color,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
