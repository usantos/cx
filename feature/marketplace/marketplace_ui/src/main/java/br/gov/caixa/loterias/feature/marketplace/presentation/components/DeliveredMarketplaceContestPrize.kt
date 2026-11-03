package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplacePrizeWords
import br.gov.caixa.loterias.feature.marketplace.ui.R as DeliveryR

private const val CONTEST_HEADING_TEXT_SIZE = 16
private const val CONTEST_PRIZE_TEXT_SIZE = 28
private const val CONTEST_PRIZE_WORDS_TEXT_SIZE = 22

@Composable
internal fun MarketplaceContestPrize(game: LotteryGameParameters, colors: MarketplaceColors) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(
        Modifier.fillMaxWidth().height(92.dp).background(colors.dark),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            formatBrazilianCurrency(game.estimatedPrize),
            style = marketplaceText(CONTEST_PRIZE_TEXT_SIZE, FontWeight.Bold),
            color = colors.darkContent,
            modifier = Modifier.uiId("pool.0006")
        )
        marketplacePrizeWords(game.estimatedPrize).takeIf(String::isNotBlank)?.let {
            Text(
                it,
                style = marketplaceText(CONTEST_PRIZE_WORDS_TEXT_SIZE, FontWeight.Bold),
                color = colors.darkContent,
                modifier = Modifier.uiId("pool.0007")
            )
        }
        Text(
            uiStrings.getString(DeliveryR.string.ui_premio_estimado_63dd56),
            style = marketplaceText(CONTEST_HEADING_TEXT_SIZE),
            color = colors.darkContent,
            modifier = Modifier.uiId("pool.0008")
        )
    }
}
