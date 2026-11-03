package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem

// Gestos, acessibilidade e menu compartilham o estado desta linha da lista.
@Composable
fun RapidBetCard(item: CartItem, onRemove: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val style = loteriasCaixaDSLotteryStyle(item.modalityCode.toIntOrNull() ?: 0, item.title)
    var opened by remember(item.id) { mutableStateOf(false) }
    var dragging by remember(item.id) { mutableStateOf(false) }
    var dragOffset by remember(item.id) { mutableStateOf(0f) }
    var contextMenu by remember(item.id) { mutableStateOf(false) }
    val panelWidth = with(LocalDensity.current) { 229.dp.toPx() }
    val offset by animateFloatAsState(
        if (dragging) {
            dragOffset
        } else if (opened) {
            panelWidth
        } else {
            0f
        },
        animationSpec = if (dragging) snap() else tween(DETAILS_ANIMATION_DURATION_MILLIS),
        label = uiStrings.getString(DeliveryR.string.ui_detalhes_da_aposta_82fbc3)
    )
    Card(
        Modifier.fillMaxWidth().padding(10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = LoteriasCaixaDSColorTokens.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).semantics {
                customActions = listOf(
                    CustomAccessibilityAction(uiStrings.getString(DeliveryR.string.ui_excluir_aposta_86e1ac)) {
                        onRemove()
                        true
                    },
                    CustomAccessibilityAction(
                        if (opened) {
                            uiStrings.getString(DeliveryR.string.ui_fechar_detalhes_91bd3b)
                        } else {
                            uiStrings.getString(
                                DeliveryR.string.ui_mostrar_detalhes_ce119f
                            )
                        }
                    ) {
                        opened = !opened
                        true
                    }
                )
            }.uiId("bet.0197").pointerInput(item.id, panelWidth) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        dragging = true
                        dragOffset = if (opened) panelWidth else 0f
                    },
                    onDragEnd = {
                        opened = dragOffset > panelWidth / 2
                        dragging = false
                    },
                    onDragCancel = { dragging = false }
                ) { change, amount ->
                    change.consume()
                    dragOffset = (dragOffset + amount).coerceIn(0f, panelWidth)
                }
            }
        ) {
            RapidBetDetails(item, style, offset, panelWidth)
            RapidBetCardForeground(
                item,
                style,
                offset,
                contextMenu,
                onCloseDetails = { opened = false },
                onShowMenu = { contextMenu = true },
                onDismissMenu = { contextMenu = false },
                onRemove = {
                    contextMenu = false
                    onRemove()
                }
            )
        }
    }
}

private const val DETAILS_ANIMATION_DURATION_MILLIS = 200
