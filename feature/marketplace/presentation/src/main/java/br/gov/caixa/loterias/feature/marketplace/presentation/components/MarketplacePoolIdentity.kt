package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplacePoolModalityLabel
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceCity
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceListRetailerName

private const val CAROUSEL_CAPTION_TEXT_SIZE = 14
private const val CAROUSEL_VALUE_TEXT_SIZE = 18
private const val CAROUSEL_PRIZE_TEXT_SIZE = 22

@Composable
internal fun androidx.compose.foundation.layout.BoxScope.MarketplacePoolIdentity(
    pool: MarketplacePool,
    lotteryFiltered: Boolean,
    updatingFavorite: Boolean,
    favorite: () -> Unit,
    colors: MarketplaceColors,
    modalityNames: Map<Pair<String, Int>, String>,
    specialContests: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (lotteryFiltered) {
        Column(Modifier.fillMaxWidth().offset(y = 24.dp).height(114.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                marketplacePoolModalityLabel(pool, modalityNames, specialContests),
                style = marketplaceText(CAROUSEL_VALUE_TEXT_SIZE, FontWeight.Bold),
                color = colors.dark,
                modifier = Modifier.uiId("pool.0020")
            )
            val prize = pool.estimatedPrizeValue()
            prize?.let {
                Text(
                    formatBrazilianCurrency(it),
                    style = marketplaceText(CAROUSEL_PRIZE_TEXT_SIZE, FontWeight.Bold),
                    color = colors.dark,
                    modifier = Modifier.uiId("pool.0021")
                )
            }
            Text(
                uiStrings.getString(LocalizedR.string.ui_premio_estimado_do_concurso_1_s_f15d0b, pool.contest.toString()),
                style = marketplaceText(CAROUSEL_CAPTION_TEXT_SIZE),
                color = LoteriasCaixaDSColorTokens.SocialGraphite,
                modifier = Modifier.uiId("pool.0022")
            )
        }
    } else {
        Box(
            Modifier.fillMaxWidth().padding(start = 37.dp, end = 38.dp).offset(y = 24.dp).height(57.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                marketplaceListRetailerName(pool.lotteryName),
                style = marketplaceText(CAROUSEL_VALUE_TEXT_SIZE, FontWeight.SemiBold),
                color = LoteriasCaixaDSColorTokens.RetailSearchText,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.uiId("pool.0023")
            )
        }
        Box(
            Modifier.fillMaxWidth().padding(start = 37.dp, end = 38.dp).offset(y = 81.dp).height(57.dp).padding(top = 8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                marketplaceCity(pool.cityAndState),
                style = marketplaceText(CAROUSEL_VALUE_TEXT_SIZE),
                color = LoteriasCaixaDSColorTokens.SocialGraphite,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.uiId("pool.0024")
            )
        }
        MarketplacePoolFavorite(pool, updatingFavorite, favorite, colors)
    }
}
