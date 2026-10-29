package br.gov.caixa.loterias.feature.results.presentation.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSCard
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSEmptyState
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSSectionHeader
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.R as LocalizedR
import br.gov.caixa.loterias.feature.results.presentation.components.DrawNumberColumns
import br.gov.caixa.loterias.feature.results.presentation.components.DrawResultSupplement
import br.gov.caixa.loterias.feature.results.presentation.components.DrawSportsResults
import br.gov.caixa.loterias.feature.results.presentation.components.confirmedBetFirstDrawHitCount
import br.gov.caixa.loterias.feature.results.presentation.components.drawResultTitle
import br.gov.caixa.loterias.feature.results.presentation.viewmodel.ConfirmedBetResultsViewModel
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

@Composable
fun ConfirmedBetResultRoute(
    bet: ConfirmedBet,
    viewModel: ConfirmedBetResultsViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onServiceRedirect: (br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit = {},
    onRedeemPrize: (() -> Unit)? = null,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lottery = caixaDSLotteryStyle(bet.modalityId, bet.modality)
    val campaignStyle = lottery.takeIf { it != resolveCaixaDSLotteryStyle(bet.modalityId, bet.modality, CaixaDSCampaign.DEFAULT) }
    val resultText = campaignStyle?.darkContent ?: CaixaDS.colors.text
    LaunchedEffect(state.serviceFailure) {
        (state.serviceFailure as? br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RedirectRequired)?.let {
            onServiceRedirect(it.destination)
        }
    }
    val serviceMessage = when (val failure = state.serviceFailure) {
        is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.NoticeRequired -> failure.notice.message ?: uiStrings.getString(
            LocalizedR.string.ui_nao_foi_possivel_consultar_o_resulta_2b3533
        )

        is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.ConnectionFailed -> uiStrings.getString(
            LocalizedR.string.ui_nao_foi_possivel_acessar_o_servico_v_856170
        )

        is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RegistrationRequired -> failure.message

        else -> null
    }
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_resultado_da_aposta_4e6334),
        onBack = onBack,
        gradientTopBar = false,
        topBarStartColor = campaignStyle?.light,
        topBarEndColor = campaignStyle?.light,
        topBarContentColor = campaignStyle?.content ?: CaixaDSColorTokens.White,
        containerColor = campaignStyle?.dark ?: CaixaDS.colors.canvas,
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(CaixaDS.colors.surface).padding(16.dp)) {
                CaixaDSPrimaryButton(
                    uiStrings.getString(LocalizedR.string.final_return_to_bets),
                    onComplete,
                    (Modifier.fillMaxWidth())
                        .uiId("results.0051")
                )
            }
        }, modifier = Modifier.uiId("results.0052")
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }

            state.errorMessage != null || serviceMessage != null -> CaixaDSEmptyState(
                title = uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_consultar_o_resulta_e18575),
                message = state.errorMessage ?: serviceMessage.orEmpty(),
                actionLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                onAction = viewModel::retry,
                modifier = (Modifier.padding(padding)).uiId("results.0053")
            )

            state.results.isNotEmpty() -> {
                Column(
                    Modifier.fillMaxSize().padding(padding).padding(16.dp).uiId("results.0054").verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    state.results.forEach { result ->
                        val drawn = result.firstDrawNumbers.toSet()
                        val hits = bet.numbers.filter(drawn::contains).toSet()
                        val hitCount = confirmedBetFirstDrawHitCount(bet, result)
                        if (campaignStyle != null) {
                            Column(Modifier.fillMaxWidth().background(campaignStyle.light).padding(16.dp)) {
                                Text(
                                    uiStrings.getString(
                                        LocalizedR.string.ui_1_s_concurso_2_s_9f3926,
                                        drawResultTitle(result, result.modality.ifBlank { bet.modality }),
                                        result.contest.toString()
                                    ),
                                    color = campaignStyle.content,
                                    style = CaixaDS.typography.titleLarge,
                                    modifier = Modifier.uiId("results.0055")
                                )
                                Text(
                                    uiStrings.getString(LocalizedR.string.ui_sorteio_realizado_em_1_s_d632f3, result.drawDate),
                                    color = campaignStyle.content,
                                    modifier = Modifier.uiId("results.0056")
                                )
                            }
                        } else {
                            CaixaDSSectionHeader(
                                "${drawResultTitle(result, result.modality.ifBlank { bet.modality })} • Concurso ${result.contest}",
                                uiStrings.getString(LocalizedR.string.ui_sorteio_realizado_em_1_s_d632f3, result.drawDate),
                                modifier = Modifier
                                    .uiId("results.0057")
                            )
                        }
                        if (result.lotecaMatches.isNotEmpty() || result.lotogolMatches.isNotEmpty()) {
                            DrawSportsResults(result, resultText)
                        } else {
                            Text(
                                uiStrings.getString(LocalizedR.string.ui_numeros_sorteados_513d0e),
                                color = resultText,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.uiId("results.0058")
                            )
                            if (result.firstDrawNumberColumns.size > 1) {
                                DrawNumberColumns(result.firstDrawNumberColumns, resultText)
                            } else {
                                NumberGrid(result.firstDrawNumbers, emptySet(), campaignStyle?.dark)
                            }
                        }
                        if (result.clovers.isNotEmpty()) {
                            Text(
                                uiStrings.getString(LocalizedR.string.ui_trevos_sorteados_2645f8),
                                color = resultText,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.uiId("results.0059")
                            )
                            NumberGrid(result.clovers, bet.clovers.filter(result.clovers::contains).toSet(), campaignStyle?.dark)
                        }
                        if (result.secondDrawNumbers.isNotEmpty()) {
                            Text(
                                uiStrings.getString(LocalizedR.string.ui_segundo_sorteio_5c48d6),
                                color = resultText,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.uiId("results.0060")
                            )
                            NumberGrid(
                                result.secondDrawNumbers,
                                bet.numbers.filter(result.secondDrawNumbers::contains).toSet(),
                                campaignStyle?.dark
                            )
                        }
                        CaixaDSCard {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    uiStrings.getString(LocalizedR.string.ui_sua_aposta_3bcfc2),
                                    color = campaignStyle?.listText ?: CaixaDS.colors.brand,
                                    style = CaixaDS.typography.titleMedium,
                                    modifier = Modifier.uiId("results.0061")
                                )
                                when {
                                    bet.lotecaSelections.isNotEmpty() -> bet.lotecaSelections.forEach { selection ->
                                        val chosen = listOfNotNull(
                                            selection.match.home.name.takeIf { selection.homeWin },
                                            "Empate".takeIf {
                                                selection.draw
                                            },
                                            selection.match.away.name.takeIf { selection.awayWin }
                                        ).joinToString(" / ")
                                        Text(
                                            uiStrings.getString(
                                                LocalizedR.string.ui_1_s_2_s_3_s_4_s_65b6b0,
                                                selection.match.number.toString(),
                                                selection.match.home.name,
                                                selection.match.away.name,
                                                chosen
                                            ),
                                            color = CaixaDS.colors.text,
                                            modifier = Modifier.uiId("results.0062")
                                        )
                                    }

                                    bet.lotogolSelections.isNotEmpty() -> bet.lotogolSelections.forEach { selection ->
                                        val homeScore = if (selection.homeScore >= 4) {
                                            uiStrings.getString(
                                                LocalizedR.string.ui_4_ou_mais_4bb81b
                                            )
                                        } else {
                                            selection.homeScore.toString()
                                        }
                                        val awayScore = if (selection.awayScore >= 4) {
                                            uiStrings.getString(
                                                LocalizedR.string.ui_4_ou_mais_4bb81b
                                            )
                                        } else {
                                            selection.awayScore.toString()
                                        }
                                        Text(
                                            uiStrings.getString(
                                                LocalizedR.string.ui_1_s_2_s_3_s_4_s_2295e5,
                                                selection.match.home.name,
                                                homeScore,
                                                awayScore,
                                                selection.match.away.name
                                            ),
                                            color = CaixaDS.colors.text,
                                            modifier = Modifier.uiId("results.0063")
                                        )
                                    }

                                    bet.numberColumns.size > 1 -> DrawNumberColumns(bet.numberColumns, CaixaDS.colors.text)

                                    else -> NumberGrid(bet.numbers, hits, campaignStyle?.dark)
                                }
                                bet.favoriteTeam?.let {
                                    Text(
                                        uiStrings.getString(LocalizedR.string.ui_time_do_coracao_1_s_7e407c, it.name),
                                        color = CaixaDS.colors.text,
                                        modifier = Modifier.uiId("results.0064")
                                    )
                                }
                                bet.luckyMonth?.let {
                                    Text(
                                        uiStrings.getString(LocalizedR.string.ui_mes_de_sorte_1_s_42ca39, it.name),
                                        color = CaixaDS.colors.text,
                                        modifier = Modifier.uiId("results.0065")
                                    )
                                }
                                if (bet.clovers.isNotEmpty()) {
                                    Text(
                                        uiStrings.getString(
                                            LocalizedR.string.ui_seus_trevos_1_s_e266c5,
                                            bet.clovers.joinToString(" • ")
                                        ),
                                        color = CaixaDS.colors.text,
                                        modifier = Modifier.uiId("results.0066")
                                    )
                                }
                                if (bet.lotecaSelections.isEmpty() && bet.lotogolSelections.isEmpty()) {
                                    Text(
                                        if (hitCount == 0) {
                                            uiStrings.getString(LocalizedR.string.format_first_draw_no_hits, bet.status)
                                        } else {
                                            uiStrings.getQuantityString(
                                                LocalizedR.plurals.format_first_draw_hits,
                                                hitCount,
                                                hitCount,
                                                bet.status
                                            )
                                        },
                                        color = if (bet.prize.signum() > 0) CaixaDS.colors.success else CaixaDS.colors.mutedText,
                                        modifier = Modifier.uiId("results.0067")
                                    )
                                }
                            }
                        }
                        DrawResultSupplement(result, resultText, includeFirstPayouts = true)
                    }
                    if (onRedeemPrize != null && bet.prize.signum() > 0 && !bet.redeemedPrize) {
                        CaixaDSPrimaryButton(
                            uiStrings.getString(LocalizedR.string.ui_resgatar_premio_e68bcd),
                            onRedeemPrize,
                            (Modifier.fillMaxWidth()).uiId("results.0068")
                        )
                    }
                }
            }

            else -> CaixaDSEmptyState(
                title = uiStrings.getString(LocalizedR.string.ui_resultado_ainda_nao_disponivel_1f07c7),
                message = uiStrings.getString(LocalizedR.string.ui_os_concursos_desta_aposta_ainda_nao_71da09),
                actionLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
                onAction = viewModel::retry,
                modifier = (Modifier.padding(padding)).uiId("results.0069")
            )
        }
    }
}

@Composable
private fun NumberGrid(numbers: List<Int>, hits: Set<Int>, campaignAccent: Color? = null) {
    FlowRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        numbers.forEach { number ->
            Box(
                Modifier.background(
                    if (campaignAccent !=
                        null
                    ) {
                        if (number in
                            hits
                        ) {
                            CaixaDSColorTokens.White
                        } else {
                            campaignAccent
                        }
                    } else if (number in
                        hits
                    ) {
                        CaixaDS.colors.success
                    } else {
                        CaixaDS.colors.brand
                    },
                    CircleShape
                ).padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    number.toString().padStart(2, '0'),
                    color = if (campaignAccent != null && number in hits) campaignAccent else CaixaDSColorTokens.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.uiId("results.0070")
                )
            }
        }
    }
}
