package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.totalFor

private const val CAROUSEL_BODY_TEXT_SIZE = 16
private const val CAROUSEL_CAPTION_TEXT_SIZE = 14
private const val CAROUSEL_VALUE_TEXT_SIZE = 18

@Composable
internal fun androidx.compose.foundation.layout.BoxScope.MarketplacePoolOffer(
    pool: MarketplacePool,
    shares: Int,
    colors: MarketplaceColors,
    onDetail: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.fillMaxWidth().offset(y = 146.dp).height(25.dp), contentAlignment = Alignment.Center) {
        Text(
            pool.prognosticLabel(),
            style = marketplaceText(CAROUSEL_BODY_TEXT_SIZE),
            color = LoteriasCaixaDSColorTokens.TicketChoiceMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("pool.0010")
        )
    }
    Box(
        Modifier.align(
            Alignment.TopCenter
        ).offset(y = 183.dp).size(88.dp, 28.dp).border(1.dp, colors.listText, RoundedCornerShape(4.dp))
            .legacyAutomationId("carousel-detail-${pool.code}").uiId("pool.0011").clickable(onClick = onDetail),
        contentAlignment = Alignment.Center
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_detalhes_62c950),
            style = marketplaceText(CAROUSEL_CAPTION_TEXT_SIZE),
            color = colors.listText,
            modifier = Modifier.uiId("pool.0012")
        )
    }
    Box(Modifier.fillMaxWidth().offset(y = 223.dp).height(25.dp), contentAlignment = Alignment.Center) {
        Text(
            pool.shareAvailability(),
            style = marketplaceText(CAROUSEL_VALUE_TEXT_SIZE),
            color = LoteriasCaixaDSColorTokens.TicketChoiceMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("pool.0013")
        )
    }
    Box(Modifier.fillMaxWidth().offset(y = 262.dp).height(26.dp), contentAlignment = Alignment.Center) {
        Text(
            formatBrazilianCurrency(pool.totalFor(shares)),
            style = marketplaceText(CAROUSEL_VALUE_TEXT_SIZE, FontWeight.Bold),
            color = LoteriasCaixaDSColorTokens.RetailSearchText,
            modifier = Modifier.uiId("pool.0014")
        )
    }
}
