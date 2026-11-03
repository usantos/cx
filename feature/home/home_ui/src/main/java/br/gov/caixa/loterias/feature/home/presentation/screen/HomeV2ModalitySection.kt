package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.presentation.components.ReorderableModalities
import br.gov.caixa.loterias.feature.home.presentation.mapper.tipoModalidade
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

@Composable
internal fun HomeV2Presentation.ModalitySection(
    cards: List<HomeLottery>,
    hasPoolCard: Boolean,
    scroll: ScrollState,
    viewport: Rect,
    showAll: Boolean,
    onShowAll: (Boolean) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.padding(top = if (hasPoolCard) 8.dp else 0.dp)) {
        Section(
            uiStrings.getString(DeliveryR.string.ui_escolha_sua_modalidade_e5181a),
            if (showAll) {
                uiStrings.getString(
                    DeliveryR.string.ui_ver_menos_8e3816
                )
            } else {
                uiStrings.getString(DeliveryR.string.ui_ver_todas_6240cd)
            }
        ) { onShowAll(!showAll) }
        Text(
            uiStrings.getString(DeliveryR.string.ui_segure_e_arraste_para_organizar_ffb77b),
            fontFamily = regular,
            fontSize = 11.sp,
            color = CxHomeV2Colors.CFF647C94,
            modifier = (Modifier.padding(top = 3.dp)).uiId("home.0147")
        )
    }
    val arrangedCards = cards.sortedBy { cardOrder.indexOf(it.tipoModalidade.name) }
    ReorderableModalities(if (showAll) arrangedCards else arrangedCards.take(COLLAPSED_MODALITY_COUNT), scroll, viewport) {
        onShowAll(true)
    }
}

private const val COLLAPSED_MODALITY_COUNT = 8
