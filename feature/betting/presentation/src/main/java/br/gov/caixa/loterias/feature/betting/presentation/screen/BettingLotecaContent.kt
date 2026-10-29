package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.LotecaOutcomeCard
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.style.lotecaLinearLayoutCenter
import br.gov.caixa.loterias.feature.betting.presentation.style.lotecaNativeTextHeight

// Fronteira declarativa existente da interface; a formatação revelou complexidade da baseline sem alterar a lógica.
@Suppress("LongMethod")
@Composable
internal fun BettingLotecaContent(
    state: BettingGameUiState,
    onOutcomeSelected: (Int, LotecaOutcome) -> Unit,
    onQuantity: () -> Unit,
    onHelp: () -> Unit,
    descriptionBindingKey: Int = 0,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val game = requireNotNull(state.game)
    val metrics = TextStyle(lineHeight = 16.sp, platformStyle = PlatformTextStyle(includeFontPadding = true))
    // TextView lê textSize do XML em pixels inteiros; a largura fracionária muda a quebra do Flow.
    val counterTextSize = with(androidx.compose.ui.platform.LocalDensity.current) { 14.sp.roundToPx().toSp() }
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth().background(CaixaDSColorTokens.SimulationToolbar).padding(horizontal = 16.dp)
        ) {
            Layout(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                content = {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_suas_apostas_15b272),
                        (
                            Modifier.padding(
                                start = 8.dp,
                                end = 8.dp
                            ).then(lotecaNativeTextHeight(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_semi_bold, 16.sp))
                            )
                            .uiId("bet.0245"),
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        fontFamily = CaixaDSFontFamily, fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp, color = CaixaDSColorTokens.SocialGraphite, style = metrics
                    )
                    Row(Modifier.uiId("bet.0246").clickable(onClick = onHelp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            uiStrings.getString(LocalizedR.string.ui_como_jogar_ceb452),
                            (
                                Modifier.padding(
                                    end = 8.dp
                                ).then(lotecaNativeTextHeight(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular, 16.sp))
                                )
                                .uiId("bet.0247"),
                            fontFamily = CaixaDSRegularFontFamily,
                            fontSize = 16.sp,
                            color = CaixaDSColorTokens.SocialGraphite,
                            style = metrics
                        )
                        Image(
                            painterResource(R.drawable.ic_info),
                            null,
                            Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(CaixaDSColorTokens.SocialGraphite)
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
            FlowRow(
                Modifier.fillMaxWidth().padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val counts = listOf(
                    "Partidas: " to state.lotecaOutcomes.count { it.value.isNotEmpty() },
                    "Simples: " to state.lotecaOutcomes.values.count { it.size == 1 },
                    "Duplas: " to state.lotecaOutcomes.values.count { it.size == 2 },
                    "Triplas: " to state.lotecaOutcomes.values.count { it.size == 3 }
                )
                counts.forEachIndexed { index, (label, count) ->
                    Row(Modifier.align(Alignment.CenterVertically), verticalAlignment = lotecaLinearLayoutCenter) {
                        if (index > 0) {
                            Box(
                                Modifier.padding(end = 8.dp).width(1.dp).height(20.dp)
                                    .background(CaixaDSColorTokens.SocialGraphite.copy(alpha = .3f))
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
                            fontFamily = CaixaDSRegularFontFamily,
                            fontSize = counterTextSize,
                            color = CaixaDSColorTokens.SocialGraphite,
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
                            fontFamily = CaixaDSFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = counterTextSize,
                            color = CaixaDSColorTokens.LotecaCounter,
                            style = metrics
                        )
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp).height(45.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(Modifier.width(112.dp).height(45.dp).legacyAutomationId("loteca-quantity")) {
                CaixaDSBettingButton(
                    "${state.numberQuantity} palpites", onQuantity,
                    Modifier.fillMaxWidth().fillMaxHeight(),
                    CaixaDSColorTokens.Loteca, CaixaDSColorTokens.White, textSize = 14f, corner = 12f,
                    icon = R.drawable.ic_arrow_down, semiBold = true,
                    verticalInset = CaixaDSBettingButtonDefaults.NativeVerticalInset
                )
            }
            Box(Modifier.height(40.dp), contentAlignment = Alignment.Center) {
                Text(
                    uiStrings.getString(
                        LocalizedR.string.ui_selecionados_1_s_2_s_0a48ce,
                        state.lotecaOutcomes.values.sumOf(Set<LotecaOutcome>::size).toString(),
                        state.numberQuantity.toString()
                    ),
                    (Modifier.then(lotecaNativeTextHeight(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular, 14.sp)))
                        .uiId("bet.0250"),
                    fontFamily = CaixaDSRegularFontFamily,
                    fontSize = 14.sp,
                    color = CaixaDSColorTokens.SocialGraphite,
                    style = metrics
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp).weight(1f).uiId("bet.0251").verticalScroll(rememberScrollState())) {
            game.matches.forEach { match ->
                val selected = state.lotecaOutcomes[match.number].orEmpty()
                val suffix = when {
                    match.legendDescription.contains("FEM.") || match.legendDescription.contains("FEMININO") -> " (feminino)"
                    match.legendDescription.contains("SUB17") -> " (sub 17)"
                    match.legendDescription.contains("SUB20") -> " (sub 20)"
                    else -> ""
                }
                Column(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_jogo_1_s_2_s_120644, match.number.toString().padStart(2, '0'), suffix),
                        (
                            Modifier.padding(
                                bottom = 8.dp
                            ).then(lotecaNativeTextHeight(br.gov.caixa.loterias.core.designsystem.R.font.caixa_std_regular, 16.sp))
                            )
                            .uiId("bet.0252"),
                        fontFamily = CaixaDSRegularFontFamily,
                        fontSize = 16.sp,
                        color = CaixaDSColorTokens.CaixaBlue,
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
            Spacer(Modifier.height(119.dp))
        }
    }
}
