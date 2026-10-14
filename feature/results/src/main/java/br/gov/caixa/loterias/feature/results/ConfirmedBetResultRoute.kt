package br.gov.caixa.loterias.feature.results

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCard
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyEmptyState
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrimaryButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacySectionHeader
import br.gov.caixa.loterias.core.domain.tickets.ConfirmedBet

@Composable
fun ConfirmedBetResultRoute(
    bet: ConfirmedBet,
    viewModel: ConfirmedBetResultsViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onServiceRedirect: (br.gov.caixa.loterias.core.domain.service.ServiceRedirect) -> Unit = {},
    onRedeemPrize: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.serviceFailure) {
        (state.serviceFailure as? br.gov.caixa.loterias.core.domain.service.ServiceFailure.RedirectRequired)?.let {
            onServiceRedirect(it.destination)
        }
    }
    val serviceMessage = when (val failure = state.serviceFailure) {
        is br.gov.caixa.loterias.core.domain.service.ServiceFailure.NoticeRequired -> failure.notice.message ?: "Não foi possível consultar o resultado."
        is br.gov.caixa.loterias.core.domain.service.ServiceFailure.ConnectionFailed -> "Não foi possível acessar o serviço. Verifique sua conexão."
        is br.gov.caixa.loterias.core.domain.service.ServiceFailure.RegistrationRequired -> failure.message
        else -> null
    }
    CaixaDSLegacyScaffold(
        title = "Resultado da aposta",
        onBack = onBack,
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(CaixaDS.colors.surface).padding(16.dp)) {
                CaixaDSLegacyPrimaryButton("Voltar às apostas", onComplete, Modifier.fillMaxWidth())
            }
        },
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }
            state.errorMessage != null || serviceMessage != null -> CaixaDSLegacyEmptyState(
                title = "Não foi possível consultar o resultado",
                message = state.errorMessage ?: serviceMessage.orEmpty(),
                actionLabel = "Tentar novamente",
                onAction = viewModel::retry,
                modifier = Modifier.padding(padding),
            )
            state.results.isNotEmpty() -> {
                Column(
                    Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    state.results.forEach { result ->
                        val drawn = result.firstDrawNumbers.toSet()
                        val hits = bet.numbers.filter(drawn::contains).toSet()
                        val hitCount = confirmedBetFirstDrawHitCount(bet, result)
                        CaixaDSLegacySectionHeader(
                            "${drawResultTitle(result, result.modality.ifBlank { bet.modality })} • Concurso ${result.contest}",
                            "Sorteio realizado em ${result.drawDate}",
                        )
                        if (result.lotecaMatches.isNotEmpty() || result.lotogolMatches.isNotEmpty()) {
                            DrawSportsResults(result, CaixaDS.colors.legacyText)
                        } else {
                            Text("Números sorteados", color = CaixaDS.colors.legacyText, fontWeight = FontWeight.Bold)
                            if (result.firstDrawNumberColumns.size > 1) DrawNumberColumns(result.firstDrawNumberColumns, CaixaDS.colors.legacyText)
                            else NumberGrid(result.firstDrawNumbers, emptySet())
                        }
                        if (result.clovers.isNotEmpty()) {
                            Text("Trevos sorteados", color = CaixaDS.colors.legacyText, fontWeight = FontWeight.Bold)
                            NumberGrid(result.clovers, bet.clovers.filter(result.clovers::contains).toSet())
                        }
                        if (result.secondDrawNumbers.isNotEmpty()) {
                            Text("Segundo sorteio", color = CaixaDS.colors.legacyText, fontWeight = FontWeight.Bold)
                            NumberGrid(result.secondDrawNumbers, bet.numbers.filter(result.secondDrawNumbers::contains).toSet())
                        }
                        CaixaDSLegacyCard {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Sua aposta", color = CaixaDS.colors.brand, style = CaixaDS.typography.titleMedium)
                                when {
                                    bet.lotecaSelections.isNotEmpty() -> bet.lotecaSelections.forEach { selection ->
                                        val chosen = listOfNotNull(selection.match.home.name.takeIf { selection.homeWin },
                                            "Empate".takeIf { selection.draw }, selection.match.away.name.takeIf { selection.awayWin }).joinToString(" / ")
                                        Text("${selection.match.number}. ${selection.match.home.name} × ${selection.match.away.name}: $chosen", color = CaixaDS.colors.legacyText)
                                    }
                                    bet.lotogolSelections.isNotEmpty() -> bet.lotogolSelections.forEach { selection ->
                                        val homeScore = if (selection.homeScore >= 4) "4 ou mais" else selection.homeScore.toString()
                                        val awayScore = if (selection.awayScore >= 4) "4 ou mais" else selection.awayScore.toString()
                                        Text("${selection.match.home.name} $homeScore × $awayScore ${selection.match.away.name}", color = CaixaDS.colors.legacyText)
                                    }
                                    bet.numberColumns.size > 1 -> DrawNumberColumns(bet.numberColumns, CaixaDS.colors.legacyText)
                                    else -> NumberGrid(bet.numbers, hits)
                                }
                                bet.favoriteTeam?.let { Text("Time do coração: ${it.name}", color = CaixaDS.colors.legacyText) }
                                bet.luckyMonth?.let { Text("Mês de sorte: ${it.name}", color = CaixaDS.colors.legacyText) }
                                if (bet.clovers.isNotEmpty()) Text("Seus trevos: ${bet.clovers.joinToString(" • ")}", color = CaixaDS.colors.legacyText)
                                if (bet.lotecaSelections.isEmpty() && bet.lotogolSelections.isEmpty()) Text(
                                    "$hitCount ${if (hitCount == 1) "acerto no primeiro sorteio" else "acertos no primeiro sorteio"} • ${bet.status}",
                                    color = if (bet.prize.signum() > 0) CaixaDS.colors.success else CaixaDS.colors.legacyMutedText,
                                )
                            }
                        }
                        DrawResultSupplement(result, CaixaDS.colors.legacyText, includeFirstPayouts = true)
                    }
                    if (onRedeemPrize != null && bet.prize.signum() > 0 && !bet.redeemedPrize) {
                        CaixaDSLegacyPrimaryButton("Resgatar prêmio", onRedeemPrize, Modifier.fillMaxWidth())
                    }
                }
            }
            else -> CaixaDSLegacyEmptyState(title = "Resultado ainda não disponível",
                message = "Os concursos desta aposta ainda não possuem resultados disponíveis.",
                actionLabel = "Tentar novamente", onAction = viewModel::retry, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
private fun NumberGrid(numbers: List<Int>, hits: Set<Int>) {
    FlowRow(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        numbers.forEach { number ->
            Box(
                Modifier.background(if (number in hits) CaixaDS.colors.success else CaixaDS.colors.brand, CircleShape).padding(10.dp),
                contentAlignment = Alignment.Center,
            ) { Text(number.toString().padStart(2, '0'), color = CaixaDSColorTokens.White, fontWeight = FontWeight.Bold) }
        }
    }
}
