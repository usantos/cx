package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.style.lotecaLinearLayoutCenter
import br.gov.caixa.loterias.feature.betting.presentation.style.lotecaNativeTextHeight

@Composable
internal fun BettingLotecaHeading(onHelp: () -> Unit, metrics: TextStyle) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Layout(
        modifier = Modifier.fillMaxWidth().padding(top = ControlText),
        content = {
            Text(
                uiStrings.getString(LocalizedR.string.ui_suas_apostas_15b272),
                (
                    Modifier.padding(
                        start = ContentGap,
                        end = ContentGap
                    ).then(lotecaNativeTextHeight(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold, ContentInsetFontSize))
                    )
                    .uiId("bet.0245"),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                fontFamily = LoteriasCaixaDSFontFamily, fontWeight = FontWeight.SemiBold,
                fontSize = ContentInsetFontSize, color = LoteriasCaixaDSColorTokens.SocialGraphite, style = metrics
            )
            Row(Modifier.uiId("bet.0246").clickable(onClick = onHelp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_como_jogar_ceb452),
                    (
                        Modifier.padding(
                            end = ContentGap
                        ).then(
                            lotecaNativeTextHeight(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular, ContentInsetFontSize)
                        )
                        )
                        .uiId("bet.0247"),
                    fontFamily = LoteriasCaixaDSRegularFontFamily,
                    fontSize = ContentInsetFontSize,
                    color = LoteriasCaixaDSColorTokens.SocialGraphite,
                    style = metrics
                )
                Image(
                    painterResource(R.drawable.ic_info),
                    null,
                    Modifier.size(IconSize),
                    colorFilter = ColorFilter.tint(LoteriasCaixaDSColorTokens.SocialGraphite)
                )
            }
        }
    ) { measurables, constraints ->
        val help = measurables[1].measure(constraints.copy(minWidth = 0, minHeight = 0))
        val title = measurables[0].measure(
            constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = (constraints.maxWidth - help.width).coerceAtLeast(0)
            )
        )
        // O Flow nativo fica ancorado à base do título; seu ícone de ajuda de 24dp pode ultrapassá-la.
        layout(constraints.maxWidth, title.height) {
            title.placeRelative(0, 0)
            help.placeRelative(constraints.maxWidth - help.width, 0)
        }
    }
}

@Composable
internal fun BettingLotecaCounters(state: BettingGameUiState, metrics: TextStyle, counterTextSize: androidx.compose.ui.unit.TextUnit) {
    FlowRow(
        Modifier.fillMaxWidth().padding(start = ContentGap, top = ContentInset, bottom = ContentInset),
        horizontalArrangement = Arrangement.spacedBy(ContentGap),
        verticalArrangement = Arrangement.spacedBy(ContentGap)
    ) {
        val counts = listOf(
            "Partidas: " to state.lotecaOutcomes.count { it.value.isNotEmpty() },
            "Simples: " to state.lotecaOutcomes.values.count { it.size == 1 },
            "Duplas: " to state.lotecaOutcomes.values.count { it.size == 2 },
            "Triplas: " to state.lotecaOutcomes.values.count { it.size == TRIPLE_OUTCOME_COUNT }
        )
        counts.forEachIndexed { index, (label, count) ->
            Row(Modifier.align(Alignment.CenterVertically), verticalAlignment = lotecaLinearLayoutCenter) {
                if (index > 0) {
                    Box(
                        Modifier.padding(end = ContentGap).width(1.dp).height(SectionGap)
                            .background(LoteriasCaixaDSColorTokens.SocialGraphite.copy(alpha = COUNTER_DIVIDER_ALPHA))
                    )
                }
                Text(
                    label,
                    (
                        Modifier.then(
                            lotecaNativeTextHeight(
                                br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular,
                                counterTextSize
                            )
                        )
                        ).uiId("bet.0248"),
                    fontFamily = LoteriasCaixaDSRegularFontFamily,
                    fontSize = counterTextSize,
                    color = LoteriasCaixaDSColorTokens.SocialGraphite,
                    style = metrics
                )
                Text(
                    count.toString(),
                    (
                        Modifier.legacyAutomationId(
                            "loteca-count-${listOf("partidas", "simples", "duplas", "triplas")[index]}"
                        ).then(
                            lotecaNativeTextHeight(
                                br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold,
                                counterTextSize
                            )
                        )
                        ).uiId("bet.0249"),
                    fontFamily = LoteriasCaixaDSFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = counterTextSize,
                    color = LoteriasCaixaDSColorTokens.LotecaCounter,
                    style = metrics
                )
            }
        }
    }
}

private val ControlText = 14.dp
private val ContentInset = 16.dp
private val ContentInsetFontSize = 16.sp
private val SectionGap = 20.dp
private val IconSize = 24.dp
private val ContentGap = 8.dp

private const val TRIPLE_OUTCOME_COUNT = 3

private const val COUNTER_DIVIDER_ALPHA = .3f
