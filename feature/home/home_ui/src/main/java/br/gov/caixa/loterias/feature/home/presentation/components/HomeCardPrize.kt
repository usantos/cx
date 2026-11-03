package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.presentation.mapper.colors
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.ui.R as DeliveryR

@Composable
internal fun CollapsedLotteryContent(lottery: HomeLottery) {
    if (lottery.kind != HomeLotteryKind.LOTTERY) {
        SpecialLotteryContent(lottery.kind)
        return
    }
    val poolResale by rememberHomePoolResale(lottery.closingDateTime, lottery.poolResaleClosingDateTime)
    val remainingTime by rememberHomeCardCountdown(lottery.closingDateTime)
    val drawDate = if (lottery.resultCode == "loteca") lottery.closingDateTime else lottery.drawDate
    Box(Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White)) {
        Image(
            painterResource(DeliveryR.drawable.legacy_home_trevos_background_card),
            null,
            Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            HomeEstimatedPrize(lottery)
            if (drawDate.isNotBlank() || lottery.closingDateTime.isNotBlank()) {
                Box(
                    Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 8.dp).height(1.dp)
                        .background(LoteriasCaixaDS.colors.line)
                )
            }
            HomeCardDrawDate(lottery, drawDate)
            if (lottery.closingDateTime.isNotBlank() && poolResale == null) {
                HomeCardClosingTime(lottery, remainingTime)
            }
            Spacer(Modifier.weight(1f))
        }
        poolResale?.let { HomePoolResaleTimer(it, Modifier.align(Alignment.Center)) }
    }
}
