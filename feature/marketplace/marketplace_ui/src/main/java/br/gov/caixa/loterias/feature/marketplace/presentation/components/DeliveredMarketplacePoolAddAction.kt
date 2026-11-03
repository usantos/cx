package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.ui.R as DeliveryR

private const val CAROUSEL_BODY_TEXT_SIZE = 16

@Composable
internal fun androidx.compose.foundation.layout.BoxScope.MarketplacePoolAddAction(
    pool: MarketplacePool,
    adding: Boolean,
    colors: MarketplaceColors,
    onAdd: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.align(Alignment.TopCenter).offset(y = 376.dp).size(208.dp, 70.dp).clip(RoundedCornerShape(10.dp))
            .background(colors.dark).legacyAutomationId("carousel-add-${pool.code}")
            .uiId("pool.0015").clickable(enabled = pool.availableShares > 0 && !adding, onClick = onAdd),
        contentAlignment = Alignment.Center
    ) {
        Text(
            buildAnnotatedString {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append(if (adding) "Adicionando" else "Adicionar")
                pop()
                append(uiStrings.getString(DeliveryR.string.ui_ao_carrinho_de_apostas_ef1eb2))
            },
            style = marketplaceText(CAROUSEL_BODY_TEXT_SIZE),
            color = colors.darkContent,
            textAlign = TextAlign.Center,
            modifier = Modifier.uiId("pool.0016")
        )
    }
}
