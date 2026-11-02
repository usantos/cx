package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.mapper.tipoModalidade
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import kotlin.math.roundToInt
import kotlinx.coroutines.isActive

@Composable
internal fun HomeV2Presentation.ReorderableModalities(items: List<HomeLottery>, scroll: ScrollState, viewport: Rect, expand: () -> Unit) {
    val bounds = remember { mutableMapOf<HomeV2Modality, Rect>() }
    var origin by remember { mutableStateOf(Offset.Zero) }
    var dragged by remember { mutableStateOf<HomeV2Modality?>(null) }
    var target by remember { mutableStateOf<HomeV2Modality?>(null) }
    var pointer by remember { mutableStateOf(Offset.Zero) }
    var grabOffset by remember { mutableStateOf(Offset.Zero) }
    val currentItems by rememberUpdatedState(items)
    val currentViewport by rememberUpdatedState(viewport)
    val currentExpand by rememberUpdatedState(expand)
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(dragged) {
        if (dragged != null) {
            val edge = with(density) { 48.dp.toPx() }
            val step = with(density) { 10.dp.toPx() }
            while (isActive) {
                withFrameNanos { }
                val view = currentViewport
                val amount = when {
                    pointer.y < view.top + edge -> -step
                    pointer.y > view.bottom - edge -> step
                    else -> 0f
                }
                if (amount != 0f) {
                    scroll.scrollBy(amount)
                    target = currentItems.firstOrNull { bounds[it.tipoModalidade]?.contains(pointer) == true }?.tipoModalidade
                }
            }
        }
    }
    Box(
        Modifier.fillMaxWidth().onGloballyPositioned { origin = it.positionInRoot() }
            .uiId("home.0103").pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { local ->
                        pointer = origin + local
                        dragged = currentItems.firstOrNull { bounds[it.tipoModalidade]?.contains(pointer) == true }?.tipoModalidade
                        target = dragged
                        dragged?.let { type ->
                            grabOffset = pointer - bounds.getValue(type).topLeft
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            currentExpand()
                        }
                    },
                    onDrag = { change, amount ->
                        if (dragged != null) {
                            change.consume()
                            pointer += amount
                            target = currentItems.firstOrNull { bounds[it.tipoModalidade]?.contains(pointer) == true }?.tipoModalidade
                        }
                    },
                    onDragEnd = {
                        val from = dragged
                        val to = target
                        dragged = null
                        target = null
                        if (from != null && to != null) moveModality(from, to)
                    },
                    onDragCancel = {
                        dragged = null
                        target = null
                    }
                )
            }
    ) {
        ModalityGrid(items, dragged, target) { type, rect -> bounds[type] = rect }
        dragged?.let { type ->
            val item = items.firstOrNull { it.tipoModalidade == type }
            val rect = bounds[type]
            if (item != null && rect != null) {
                ModalityCard(
                    item,
                    Modifier.offset {
                        val position = pointer - grabOffset - origin
                        IntOffset(position.x.roundToInt(), position.y.roundToInt())
                    }.width(with(density) { rect.width.toDp() }).zIndex(1f)
                        .shadow(8.dp, RoundedCornerShape(11.dp))
                )
            }
        }
    }
}
