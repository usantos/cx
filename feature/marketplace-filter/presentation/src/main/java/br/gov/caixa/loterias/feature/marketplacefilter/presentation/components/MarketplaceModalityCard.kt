package br.gov.caixa.loterias.feature.marketplacefilter.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import br.gov.caixa.loterias.core.designsystem.R as LoteriasCaixaDSResources
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.LoteriasCaixaDSLotteryMark
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSLotteryFilterSpecialBackground
import br.gov.caixa.loterias.core.designsystem.artwork.loteriasCaixaDSMarketplaceFilterClover
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSMarketplaceFilterStyle
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCard as Card
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCardDefaults as CardDefaults
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.loteriasCaixaDSMarketplaceFilterText as filterText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.loteriasCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.marketplacefilter.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi

@Composable
internal fun ModalityCard(modality: MarketplaceFilterModalityUi, selected: Boolean, onClick: () -> Unit) {
    val palette = loteriasCaixaDSLotteryStyle(modality.id ?: 0, modality.baseName)
    val selectedClover = if (!modality.isAll) {
        loteriasCaixaDSCampaignDrawable(
            modality.id ?: 0,
            modality.baseName,
            if (selected) LoteriasCaixaDSLotteryArtwork.FILTER_SELECTED_CLOVER else LoteriasCaixaDSLotteryArtwork.CLOVER_ON_LIGHT
        )
            ?: loteriasCaixaDSMarketplaceFilterClover(modality.id ?: 0, modality.baseName, selected)
    } else {
        null
    }
    val specialBackground = if (selected && modality.contestType == 2 && !modality.isAll) {
        loteriasCaixaDSLotteryFilterSpecialBackground(modality.id ?: 0, modality.baseName, modality.name)
    } else {
        null
    }
    ModalityCardBody(modality, selected, onClick, palette, selectedClover, specialBackground)
}

@Composable
internal fun ModalityCardBody(
    modality: MarketplaceFilterModalityUi,
    selected: Boolean,
    onClick: () -> Unit,
    palette: br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle,
    selectedClover: Int?,
    specialBackground: Int?,
) {
    val modalityColor = palette.light
    Card(
        modifier = Modifier.padding(8.dp).size(110.dp)
            .uiId("filter.0018").selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = LoteriasCaixaDSMarketplaceFilterStyle.canvas),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f).background(
                    if (selected) {
                        if (modality.isAll) LoteriasCaixaDSMarketplaceFilterStyle.accent else modalityColor
                    } else {
                        LoteriasCaixaDSColorTokens.White
                    }
                ),
                contentAlignment = Alignment.Center
            ) {
                ModalityCardArtwork(modality, selected, palette, selectedClover, specialBackground)
            }
            if (!modality.isAll) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(33.dp).background(palette.dark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modality.cardLabel,
                        color = palette.darkContent,
                        style = filterText(MODALITY_CARD_LABEL_SIZE, FontWeight.Bold).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier.uiId("filter.0022")
                    )
                }
            }
        }
    }
}

@Composable
internal fun androidx.compose.foundation.layout.BoxScope.ModalityCardArtwork(
    modality: MarketplaceFilterModalityUi,
    selected: Boolean,
    palette: br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSLotteryStyle,
    selectedClover: Int?,
    specialBackground: Int?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
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
            color = if (selected) LoteriasCaixaDSColorTokens.White else LoteriasCaixaDSMarketplaceFilterStyle.accent,
            style = filterText(ALL_MODALITIES_LABEL_SIZE, FontWeight.Bold).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily),
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
        LoteriasCaixaDSLotteryMark(
            modifier = (Modifier.size(33.dp)).uiId("filter.0020"),
            lightColor = if (selected) palette.content else palette.light,
            accentColor = if (selected) {
                palette.content.copy(
                    alpha = MODALITY_MARK_ACCENT_ALPHA
                )
            } else {
                palette.light.copy(alpha = MODALITY_MARK_ACCENT_ALPHA)
            }
        )
    }
    if (selected) {
        Icon(
            painterResource(LoteriasCaixaDSResources.drawable.legacy_marketplace_filter_check),
            null,
            (Modifier.align(Alignment.TopEnd).padding(top = 5.dp, end = 3.dp).size(width = 11.dp, height = 8.dp))
                .uiId("filter.0021"),
            tint = palette.content
        )
    }
}

private const val MODALITY_CARD_LABEL_SIZE = 12
private const val ALL_MODALITIES_LABEL_SIZE = 14
private const val MODALITY_MARK_ACCENT_ALPHA = 0.5f
