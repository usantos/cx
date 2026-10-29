package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState

@Composable
internal fun HomeLegacyPagerCard(
    lottery: HomeLottery,
    resultState: ResultsUiState,
    resultExpanded: Boolean,
    onToggleResult: () -> Unit,
    distance: () -> Float,
) {
    LotteryCard(
        lottery = lottery,
        resultState = resultState,
        resultExpanded = resultExpanded,
        onToggleResult = onToggleResult,
        modifier = Modifier.graphicsLayer {
            // O cartão em foco cresce continuamente enquanto o paginador se estabiliza. Os cartões
            // adjacentes encolhem nos dois eixos, como no ScaleTransformer legado.
            val scale = carouselCardScale(distance())
            scaleX = scale
            scaleY = scale
        }
    )
}

@Composable
internal fun BoxScope.HomeLegacyCardActions(
    homeState: HomeUiState,
    selectedLottery: HomeLottery,
    showPoolButton: Boolean,
    quickAccessExpanded: Boolean,
    actions: HomeActions,
) {
    if (showPoolButton) {
        HomePoolAction(
            selectedLottery,
            !quickAccessExpanded && !homeState.preparingBet,
            { actions.pool(selectedLottery) },
            (
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(252.dp)
                    .padding(start = 88.dp, end = 88.dp, bottom = 168.dp).padding(vertical = 6.dp)
                ).uiId("home.0015")
        )
    }
    HomeBetAction(
        selectedLottery,
        !quickAccessExpanded && !homeState.preparingBet &&
            (selectedLottery.open || selectedLottery.kind == HomeLotteryKind.LOTTERY),
        { actions.bet(selectedLottery) },
        (
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(152.dp)
                .padding(start = 88.dp, end = 88.dp, bottom = 68.dp).padding(vertical = 6.dp)
            ).uiId("home.0016")
    )
}

@Composable
internal fun HomePoolAction(
    lottery: HomeLottery,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp),
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        modifier.shadow(2.dp, RoundedCornerShape(4.dp))
            .background(
                if (lottery.octoberRoseTheme) CaixaDSColorTokens.White else lottery.darkColor,
                RoundedCornerShape(if (lottery.octoberRoseTheme) 10.dp else 4.dp)
            )
            .then(if (lottery.octoberRoseTheme) Modifier.border(1.dp, lottery.darkColor, RoundedCornerShape(10.dp)) else Modifier)
            .uiId("home.0017").clickable(enabled = enabled, role = androidx.compose.ui.semantics.Role.Button, onClick = onClick),
        Alignment.Center
    ) {
        Text(
            uiStrings.getString(LocalizedR.string.ui_compre_seu_bolao_97d03e),
            (Modifier.then(if (LocalDensity.current.fontScale > 1f) Modifier.fillMaxWidth() else Modifier).padding(contentPadding))
                .uiId("home.0018"),
            color = if (lottery.octoberRoseTheme) lottery.darkColor else lottery.darkContent,
            style = homeLegacyTextStyle(16.sp, FontWeight.Bold),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun HomeBetAction(
    lottery: HomeLottery,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp),
) {
    Box(
        modifier.shadow(2.dp, RoundedCornerShape(4.dp))
            .background(CaixaDSColorTokens.White, RoundedCornerShape(4.dp))
            .border(
                2.dp,
                if (lottery.resultCode ==
                    "timemania"
                ) {
                    lottery.headerContentColor
                } else {
                    lottery.darkColor
                },
                RoundedCornerShape(4.dp)
            )
            .uiId("home.0019").clickable(enabled = enabled, role = androidx.compose.ui.semantics.Role.Button, onClick = onClick),
        Alignment.Center
    ) {
        Text(
            if (lottery.kind == HomeLotteryKind.LOTTERY && lottery.minimumBetAmount.signum() > 0) {
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Aposte") }
                    append("\npor ${formatBrazilianCurrency(lottery.minimumBetAmount)}")
                }
            } else {
                buildAnnotatedString { withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Aposte") } }
            },
            (Modifier.then(if (LocalDensity.current.fontScale > 1f) Modifier.fillMaxWidth() else Modifier).padding(contentPadding))
                .uiId("home.0020"),
            color = if (lottery.kind == HomeLotteryKind.LOTTERY && lottery.resultCode != "timemania") {
                lottery.darkColor
            } else {
                lottery.headerContentColor
            },
            style = homeLegacyTextStyle(16.sp),
            textAlign = TextAlign.Center
        )
    }
}
