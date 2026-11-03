package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.totalFor
import br.gov.caixa.loterias.feature.marketplace.ui.R as DeliveryR

@Composable
internal fun LargeFontPoolOffer(pool: MarketplacePool, shares: Int, colors: MarketplaceColors, onDetail: () -> Unit) {
    Text(
        pool.prognosticLabel(),
        (Modifier.fillMaxWidth()).uiId("pool.0082"),
        style = marketplaceText(CAROUSEL_BODY_SIZE),
        color = LoteriasCaixaDSColorTokens.TicketChoiceMuted,
        textAlign = TextAlign.Center
    )
    LargeFontPoolAction(
        stringResource(DeliveryR.string.ui_detalhes_62c950),
        "carousel-detail-${pool.code}",
        colors,
        onDetail
    )
    Text(
        pool.shareAvailability(),
        (Modifier.fillMaxWidth()).uiId("pool.0083"),
        style = marketplaceText(CAROUSEL_VALUE_SIZE),
        color = LoteriasCaixaDSColorTokens.TicketChoiceMuted,
        textAlign = TextAlign.Center
    )
    Text(
        formatBrazilianCurrency(pool.totalFor(shares)),
        (Modifier.fillMaxWidth()).uiId("pool.0084"),
        style = marketplaceText(CAROUSEL_VALUE_SIZE, FontWeight.Bold),
        color = LoteriasCaixaDSColorTokens.RetailSearchText,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun LargeFontPoolAction(label: String, tag: String, colors: MarketplaceColors, action: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().heightIn(min = 48.dp).border(1.dp, colors.listText, RoundedCornerShape(4.dp))
            .legacyAutomationId(tag).uiId("pool.0093").clickable(role = Role.Button, onClick = action).padding(8.dp)
    ) {
        Text(
            label,
            (Modifier.fillMaxWidth()).uiId("pool.0094"),
            style = marketplaceText(CAROUSEL_CAPTION_SIZE),
            color = colors.listText,
            textAlign = TextAlign.Center
        )
    }
}
