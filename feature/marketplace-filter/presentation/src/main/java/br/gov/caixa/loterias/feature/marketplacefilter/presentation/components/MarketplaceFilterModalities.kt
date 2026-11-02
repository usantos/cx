package br.gov.caixa.loterias.feature.marketplacefilter.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterLabel as Label
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState

@Composable
internal fun MarketplaceFilterModalities(state: MarketplaceFilterUiState, onAction: (MarketplaceFilterAction) -> Unit) {
    Label("Modalidades", (Modifier.padding(start = 10.dp, top = 10.dp)).uiId("filter.0016"))
    LazyRow(modifier = (Modifier.fillMaxWidth().padding(top = 16.dp)).uiId("filter.0017")) {
        items(state.modalities) { modality ->
            ModalityCard(
                modality = modality,
                selected = when {
                    state.allModalitiesHighlighted -> modality.isAll

                    state.selectedModality != null ->
                        modality.id == state.selectedModality.id &&
                            modality.contestType == state.selectedModality.contestType

                    else -> modality == state.modalities.firstOrNull()
                },
                onClick = { onAction(MarketplaceFilterAction.SelectModality(modality)) }
            )
        }
    }
}
