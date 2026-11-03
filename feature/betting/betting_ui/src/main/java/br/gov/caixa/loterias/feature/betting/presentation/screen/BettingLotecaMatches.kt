package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.components.LotecaOutcomeCard
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.style.lotecaNativeTextHeight
import br.gov.caixa.loterias.feature.betting.ui.R as DeliveryR

@Composable
fun BettingLotecaMatchesDelivered(
    state: BettingGameUiState,
    onOutcomeSelected: (Int, LotecaOutcome) -> Unit,
    descriptionBindingKey: Int,
    metrics: TextStyle,
    modifier: Modifier,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(modifier) {
        requireNotNull(state.game).matches.forEach { match ->
            val selected = state.lotecaOutcomes[match.number].orEmpty()
            val suffix = when {
                match.legendDescription.contains("FEM.") || match.legendDescription.contains("FEMININO") -> " (feminino)"
                match.legendDescription.contains("SUB17") -> " (sub 17)"
                match.legendDescription.contains("SUB20") -> " (sub 20)"
                else -> ""
            }
            Column(Modifier.fillMaxWidth().padding(top = ContentInset)) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_jogo_1_s_2_s_120644, match.number.toString().padStart(2, '0'), suffix),
                    (
                        Modifier.padding(
                            bottom = ContentGap
                        ).lotecaNativeTextHeight(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular, ContentInsetFontSize)
                        )
                        .uiId("bet.0252"),
                    fontFamily = LoteriasCaixaDSRegularFontFamily,
                    fontSize = ContentInsetFontSize,
                    color = LoteriasCaixaDSColorTokens.CaixaBlue,
                    style = metrics
                )
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    LotecaOutcome.entries.forEachIndexed { index, outcome ->
                        val team = when (index) {
                            0 -> match.home
                            2 -> match.away
                            else -> null
                        }
                        LotecaOutcomeCard(
                            index = index,
                            team = team,
                            selected = outcome in selected,
                            descriptionBindingKey = descriptionBindingKey,
                            onClick = { onOutcomeSelected(match.number, outcome) },
                            modifier = (Modifier.weight(1f).fillMaxHeight().legacyAutomationId("loteca-${match.number}-$outcome"))
                                .uiId("bet.0253")
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(MatchListBottomInset))
    }
}

private val MatchListBottomInset = 119.dp
private val ContentInset = 16.dp
private val ContentInsetFontSize = 16.sp
private val ContentGap = 8.dp
