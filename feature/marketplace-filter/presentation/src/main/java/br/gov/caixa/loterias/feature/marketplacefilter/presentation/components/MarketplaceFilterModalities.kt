package br.gov.caixa.loterias.feature.marketplacefilter.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R as CaixaDSResources
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSLotteryFilterSpecialBackground
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterLabel as Label
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplaceFilterStyle
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.caixaDSMarketplaceFilterText as filterText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.marketplacefilter.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi
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

@Composable
private fun ModalityCard(modality: MarketplaceFilterModalityUi, selected: Boolean, onClick: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val palette = caixaDSLotteryStyle(modality.id ?: 0, modality.baseName)
    val modalityColor = palette.light
    val selectedClover = if (!modality.isAll) {
        caixaDSCampaignDrawable(
            modality.id ?: 0,
            modality.baseName,
            if (selected) CaixaDSLotteryArtwork.FILTER_SELECTED_CLOVER else CaixaDSLotteryArtwork.CLOVER_ON_LIGHT
        )
            ?: caixaDSMarketplaceFilterClover(modality.id ?: 0, modality.baseName, selected)
    } else {
        null
    }
    val specialBackground = if (selected && modality.contestType == 2 && !modality.isAll) {
        caixaDSLotteryFilterSpecialBackground(modality.id ?: 0, modality.baseName, modality.name)
    } else {
        null
    }
    ModalityCardBody(modality, selected, onClick, palette, selectedClover, specialBackground)
}

@Composable
private fun ModalityCardBody(
    modality: MarketplaceFilterModalityUi,
    selected: Boolean,
    onClick: () -> Unit,
    palette: br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle,
    selectedClover: Int?,
    specialBackground: Int?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val modalityColor = palette.light
    Card(
        modifier = Modifier.padding(8.dp).size(110.dp)
            .uiId("filter.0018").selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CaixaDSMarketplaceFilterStyle.canvas),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f).background(
                    if (selected) {
                        if (modality.isAll) CaixaDSMarketplaceFilterStyle.accent else modalityColor
                    } else {
                        CaixaDSColorTokens.White
                    }
                ),
                contentAlignment = Alignment.Center
            ) {
                specialBackground?.let {
                    // O adaptador legado define este fundo no topo do cartão, atrás do trevo.
                    Image(
                        painter = painterResource(it),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }
                if (modality.isAll) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_todas_as_modalidades_d0b87a),
                        color = if (selected) CaixaDSColorTokens.White else CaixaDSMarketplaceFilterStyle.accent,
                        style = filterText(14, FontWeight.Bold).copy(fontFamily = CaixaDSFuturaFontFamily),
                        modifier = Modifier
                            .uiId("filter.0019")
                    )
                } else if (selectedClover != null) {
                    Image(
                        painter = painterResource(selectedClover),
                        contentDescription = null,
                        modifier = Modifier.size(33.dp)
                    )
                } else {
                    CaixaDSLotteryMark(
                        modifier = (Modifier.size(33.dp)).uiId("filter.0020"),
                        lightColor = if (selected) palette.content else palette.light,
                        accentColor = if (selected) palette.content.copy(alpha = 0.5f) else palette.light.copy(alpha = 0.5f)
                    )
                }
                if (selected) {
                    Icon(
                        painterResource(CaixaDSResources.drawable.legacy_marketplace_filter_check),
                        null,
                        (Modifier.align(Alignment.TopEnd).padding(top = 5.dp, end = 3.dp).size(width = 11.dp, height = 8.dp))
                            .uiId("filter.0021"),
                        tint = palette.content
                    )
                }
            }
            if (!modality.isAll) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(33.dp).background(palette.dark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modality.cardLabel,
                        color = palette.darkContent,
                        style = filterText(12, FontWeight.Bold).copy(fontFamily = CaixaDSFuturaFontFamily),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier.uiId("filter.0022")
                    )
                }
            }
        }
    }
}
