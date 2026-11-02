package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.mapper.tipoModalidade
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery

@Composable
internal fun HomeV2Presentation.ModalityGrid(
    items: List<HomeLottery>,
    dragged: HomeV2Modality?,
    target: HomeV2Modality?,
    onBounds: (HomeV2Modality, Rect) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { item ->
                    key(item.tipoModalidade) {
                        val type = item.tipoModalidade
                        val index = items.indexOf(item)
                        ModalityCard(
                            item,
                            Modifier.weight(1f)
                                .onGloballyPositioned { onBounds(type, it.boundsInRoot()) }
                                .alpha(if (dragged == type) DRAGGED_MODALITY_ALPHA else 1f)
                                .then(
                                    if (target == type &&
                                        dragged != type
                                    ) {
                                        Modifier.border(2.dp, blue, RoundedCornerShape(11.dp))
                                    } else {
                                        Modifier
                                    }
                                )
                                .semantics {
                                    customActions = buildList {
                                        if (index > 0) {
                                            add(
                                                CustomAccessibilityAction(
                                                    uiStrings.getString(LocalizedR.string.ui_mover_para_posicao_anterior_0ea756)
                                                ) {
                                                    moveModality(type, items[index - 1].tipoModalidade)
                                                    true
                                                }
                                            )
                                        }
                                        if (index < items.lastIndex) {
                                            add(
                                                CustomAccessibilityAction(
                                                    uiStrings.getString(LocalizedR.string.ui_mover_para_proxima_posicao_92b1b5)
                                                ) {
                                                    moveModality(type, items[index + 1].tipoModalidade)
                                                    true
                                                }
                                            )
                                        }
                                    }
                                }
                        )
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

private const val DRAGGED_MODALITY_ALPHA = .3f
