package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool

@Composable
internal fun LargeFontPoolAddAction(pool: MarketplacePool, adding: Boolean, colors: MarketplaceColors, onAdd: () -> Unit) {
    val addText = buildAnnotatedString {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(if (adding) "Adicionando" else "Adicionar")
        pop()
        append(stringResource(R.string.ui_ao_carrinho_de_apostas_ef1eb2))
    }
    Box(
        Modifier.fillMaxWidth().heightIn(min = 70.dp).clip(RoundedCornerShape(10.dp)).background(colors.dark)
            .legacyAutomationId("carousel-add-${pool.code}")
            .uiId("pool.0086").clickable(
                enabled = pool.availableShares > 0 && !adding,
                role = Role.Button,
                onClick = onAdd
            ).padding(12.dp)
    ) {
        Text(
            addText,
            (Modifier.fillMaxWidth()).uiId("pool.0087"),
            style = marketplaceText(CAROUSEL_BODY_SIZE),
            color = colors.darkContent,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun LargeFontShareQuantity(
    pool: MarketplacePool,
    shares: Int,
    adding: Boolean,
    colors: MarketplaceColors,
    actions: MarketplaceCardActions,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        PoolQuantityButton("−", shares > 1 && !adding, colors, "carousel-decrease-${pool.code}", actions.decrease)
        Text(
            shares.toString(),
            (Modifier.weight(1f)).uiId("pool.0095"),
            style = marketplaceText(CAROUSEL_VALUE_SIZE),
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            textAlign = TextAlign.Center
        )
        PoolQuantityButton(
            "+",
            shares < pool.availableShares && !adding,
            colors,
            "carousel-increase-${pool.code}",
            actions.increase
        )
    }
}
