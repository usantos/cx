package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.marketplace.R
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplacePoolModalityLabel
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.totalFor
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceCity
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceListRetailerName

@Composable
internal fun MarketplaceLargeFontCarouselCard(
    pool: MarketplacePool,
    shares: Int,
    lotteryFiltered: Boolean,
    updatingFavorite: Boolean,
    adding: Boolean,
    actions: MarketplaceCardActions,
    modalityNames: Map<Pair<String, Int>, String>,
    specialContests: MarketplaceSpecialContests,
) {
    val colors = marketplaceColors(pool.modality)
    Column(
        Modifier.width(280.dp).height(461.dp).background(CaixaDSColorTokens.White)
            .uiId("pool.0081").verticalScroll(rememberScrollState())
    ) {
        Box(
            Modifier.fillMaxWidth().height(18.dp).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(if (lotteryFiltered) colors.light else colors.carouselHeader)
        )
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LargeFontPoolIdentity(pool, lotteryFiltered, updatingFavorite, actions.favorite, colors, modalityNames, specialContests)
            Text(
                pool.prognosticLabel(),
                (Modifier.fillMaxWidth()).uiId("pool.0082"),
                style = marketplaceText(CAROUSEL_BODY_SIZE),
                color = CaixaDSColorTokens.TicketChoiceMuted,
                textAlign = TextAlign.Center
            )
            LargeFontPoolAction(
                stringResource(R.string.ui_detalhes_62c950),
                "carousel-detail-${pool.code}",
                colors,
                actions.detail
            )
            Text(
                pool.shareAvailability(),
                (Modifier.fillMaxWidth()).uiId("pool.0083"),
                style = marketplaceText(CAROUSEL_VALUE_SIZE),
                color = CaixaDSColorTokens.TicketChoiceMuted,
                textAlign = TextAlign.Center
            )
            Text(
                formatBrazilianCurrency(pool.totalFor(shares)),
                (Modifier.fillMaxWidth()).uiId("pool.0084"),
                style = marketplaceText(CAROUSEL_VALUE_SIZE, FontWeight.Bold),
                color = CaixaDSColorTokens.RetailSearchText,
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(R.string.ui_quantidade_de_cotas_ddb3fd),
                (Modifier.fillMaxWidth()).uiId("pool.0085"),
                style = marketplaceText(CAROUSEL_BODY_SIZE),
                color = CaixaDSColorTokens.TicketChoiceMuted,
                textAlign = TextAlign.Center
            )
            LargeFontShareQuantity(pool, shares, adding, colors, actions)
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
                        onClick = actions.add
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
    }
}

@Composable
private fun LargeFontPoolIdentity(
    pool: MarketplacePool,
    lotteryFiltered: Boolean,
    updatingFavorite: Boolean,
    favorite: () -> Unit,
    colors: MarketplaceColors,
    modalityNames: Map<Pair<String, Int>, String>,
    specialContests: MarketplaceSpecialContests,
) {
    if (lotteryFiltered) {
        Text(
            marketplacePoolModalityLabel(pool, modalityNames, specialContests),
            (Modifier.fillMaxWidth()).uiId("pool.0088"),
            style = marketplaceText(CAROUSEL_VALUE_SIZE, FontWeight.Bold),
            color = colors.dark,
            textAlign = TextAlign.Center
        )
        pool.estimatedPrizeValue()?.let { prize ->
            Text(
                formatBrazilianCurrency(prize),
                (Modifier.fillMaxWidth()).uiId("pool.0089"),
                style = marketplaceText(CAROUSEL_PRIZE_SIZE, FontWeight.Bold),
                color = colors.dark,
                textAlign = TextAlign.Center
            )
        }
        Text(
            stringResource(R.string.ui_premio_estimado_do_concurso_1_s_f15d0b, pool.contest.toString()),
            (Modifier.fillMaxWidth()).uiId("pool.0090"),
            style = marketplaceText(CAROUSEL_CAPTION_SIZE),
            color = CaixaDSColorTokens.SocialGraphite,
            textAlign = TextAlign.Center
        )
    } else {
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(end = 40.dp)) {
                Text(
                    marketplaceListRetailerName(pool.lotteryName),
                    (Modifier.fillMaxWidth()).uiId("pool.0091"),
                    style = marketplaceText(CAROUSEL_VALUE_SIZE, FontWeight.SemiBold),
                    color = CaixaDSColorTokens.RetailSearchText
                )
                Text(
                    marketplaceCity(pool.cityAndState),
                    (Modifier.fillMaxWidth()).uiId("pool.0092"),
                    style = marketplaceText(CAROUSEL_VALUE_SIZE),
                    color = CaixaDSColorTokens.SocialGraphite
                )
            }
            MarketplacePoolFavorite(pool, updatingFavorite, favorite, colors)
        }
    }
}

@Composable
private fun LargeFontPoolAction(label: String, tag: String, colors: MarketplaceColors, action: () -> Unit) {
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

private const val CAROUSEL_BODY_SIZE = 16
private const val CAROUSEL_VALUE_SIZE = 18
private const val CAROUSEL_PRIZE_SIZE = 22
private const val CAROUSEL_CAPTION_SIZE = 14

@Composable
private fun LargeFontShareQuantity(
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
            color = CaixaDSColorTokens.SocialGraphite,
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
