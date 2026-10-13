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
    viewModel: ResultsViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(bet.modality, bet.contest) { viewModel.loadContest(bet.modality, bet.contest) }
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
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.brand)
            }
            state.errorMessage != null -> CaixaDSLegacyEmptyState(
                title = "Não foi possível consultar o resultado",
                message = state.errorMessage.orEmpty(),
                actionLabel = "Tentar novamente",
                onAction = viewModel::retry,
                modifier = Modifier.padding(padding),
            )
            state.result != null -> {
                val result = requireNotNull(state.result)
                val drawn = (result.firstDrawNumbers + result.secondDrawNumbers).toSet()
                val hits = bet.numbers.filter(drawn::contains).toSet()
                Column(
                    Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    CaixaDSLegacySectionHeader(
                        "${result.modality.ifBlank { bet.modality }} • Concurso ${result.contest}",
                        "Sorteio realizado em ${result.drawDate}",
                    )
                    Text("Números sorteados", color = CaixaDS.colors.legacyText, fontWeight = FontWeight.Bold)
                    NumberGrid(result.firstDrawNumbers, emptySet())
                    if (result.secondDrawNumbers.isNotEmpty()) {
                        Text("Segundo sorteio", color = CaixaDS.colors.legacyText, fontWeight = FontWeight.Bold)
                        NumberGrid(result.secondDrawNumbers, emptySet())
                    }
                    CaixaDSLegacyCard {
                        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Sua aposta", color = CaixaDS.colors.brand, style = CaixaDS.typography.titleMedium)
                            NumberGrid(bet.numbers, hits)
                            Text(
                                "${hits.size} ${if (hits.size == 1) "acerto" else "acertos"} • ${bet.status.ifBlank { if (bet.prize.signum() > 0) "Premiada" else "Não premiada" }}",
                                color = if (bet.prize.signum() > 0) CaixaDS.colors.success else CaixaDS.colors.legacyMutedText,
                            )
                        }
                    }
                }
            }
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
