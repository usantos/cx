package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.marketplace.R
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplacePoolModalityLabel
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceCity
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceListRetailerName

@Composable
internal fun LargeFontPoolIdentity(
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
            color = LoteriasCaixaDSColorTokens.SocialGraphite,
            textAlign = TextAlign.Center
        )
    } else {
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(end = 40.dp)) {
                Text(
                    marketplaceListRetailerName(pool.lotteryName),
                    (Modifier.fillMaxWidth()).uiId("pool.0091"),
                    style = marketplaceText(CAROUSEL_VALUE_SIZE, FontWeight.SemiBold),
                    color = LoteriasCaixaDSColorTokens.RetailSearchText
                )
                Text(
                    marketplaceCity(pool.cityAndState),
                    (Modifier.fillMaxWidth()).uiId("pool.0092"),
                    style = marketplaceText(CAROUSEL_VALUE_SIZE),
                    color = LoteriasCaixaDSColorTokens.SocialGraphite
                )
            }
            MarketplacePoolFavorite(pool, updatingFavorite, favorite, colors)
        }
    }
}
