package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind

/** Texto ampliado usa sua altura medida; apenas a área visível do corpo rola abaixo dos cabeçalhos originais. */
@Composable
internal fun HomeLargeFontLotteryContent(lottery: HomeLottery) {
    if (lottery.kind != HomeLotteryKind.LOTTERY) {
        Box(Modifier.fillMaxSize().uiId("home.0033").verticalScroll(rememberScrollState())) { SpecialLotteryContent(lottery.kind) }
        return
    }
    val resale by rememberHomePoolResale(lottery.closingDateTime, lottery.poolResaleClosingDateTime)
    val remaining by rememberHomeCardCountdown(lottery.closingDateTime)
    val drawDate = if (lottery.resultCode == "loteca") lottery.closingDateTime else lottery.drawDate
    Box(Modifier.fillMaxSize().background(CaixaDSColorTokens.White)) {
        Image(
            painterResource(R.drawable.legacy_home_trevos_background_card),
            null,
            Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            Modifier.fillMaxSize().legacyAutomationId("home-large-font-body")
                .uiId("home.0034").verticalScroll(rememberScrollState()).padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeEstimatedPrize(lottery, enlarged = true)
            HomeCardDrawDate(lottery, drawDate)
            if (lottery.closingDateTime.isNotBlank() && resale == null) HomeCardClosingTime(lottery, remaining)
            resale?.let { HomePoolResaleTimer(it, Modifier.fillMaxWidth().padding(top = 16.dp)) }
        }
    }
}
