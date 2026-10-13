package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.CaixaDS
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyCard
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyEmptyState
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyPrimaryButton
import br.gov.caixa.loterias.core.designsystem.CaixaDSLegacyScaffold
import br.gov.caixa.loterias.core.designsystem.CaixaDSNumberBall
import br.gov.caixa.loterias.core.domain.marketplace.MarketplaceBet

@Composable
fun MarketplaceDetailRoute(
    viewModel: MarketplaceDetailViewModel,
    onBack: () -> Unit,
    onPoolReserved: (MarketplacePoolConfirmation) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.confirmation) {
        state.confirmation?.let { confirmation ->
            viewModel.consumeConfirmation()
            onPoolReserved(confirmation)
        }
    }
    MarketplaceDetailScreen(
        state = state,
        onBack = onBack,
        onRetry = { viewModel.onAction(MarketplaceDetailAction.Retry) },
        onDecrease = { viewModel.onAction(MarketplaceDetailAction.DecreaseShares) },
        onIncrease = { viewModel.onAction(MarketplaceDetailAction.IncreaseShares) },
        onAddToCart = { viewModel.onAction(MarketplaceDetailAction.AddToCart) },
    )
}

@Composable
fun MarketplaceDetailScreen(
    state: MarketplaceDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onAddToCart: () -> Unit,
) {
    CaixaDSLegacyScaffold(title = "Detalhes do bolão", onBack = onBack) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.errorMessage != null -> DetailErrorContent(state.errorMessage, onRetry, Modifier.padding(padding))
            state.detail != null -> {
                val detail = state.detail
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Text(
                            detail.modality.ifBlank { "Bolão" },
                            color = CaixaDS.colors.brand,
                            style = CaixaDS.typography.headlineSmall,
                        )
                        Text("Concurso ${detail.contest} • ${detail.drawDateAndTime}")
                        if (detail.estimatedPrize.isNotBlank()) {
                            Text("Prêmio estimado: R$ ${detail.estimatedPrize}", fontWeight = FontWeight.Bold)
                        }
                    }
                    item {
                        CaixaDSLegacyCard {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(detail.lotteryName, color = CaixaDS.colors.brand, style = CaixaDS.typography.titleMedium)
                                if (detail.lotteryNumber.isNotBlank()) Text("Código ${detail.lotteryNumber}")
                                if (detail.cityAndState.isNotBlank()) Text(detail.cityAndState)
                                Text("${detail.betQuantity} apostas • ${detail.numberQuantity} dezenas")
                                Text("${detail.availableShares} de ${detail.totalShares} cotas disponíveis")
                            }
                        }
                    }
                    itemsIndexed(detail.bets) { index, bet -> BetCard(index + 1, bet) }
                    item {
                        CaixaDSLegacyCard {
                            Column(
                                Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text("Quantidade de cotas", color = CaixaDS.colors.brand, style = CaixaDS.typography.titleMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = onDecrease, enabled = state.canDecrease) { Text("−") }
                                    Text(state.selectedShares.toString(), style = CaixaDS.typography.headlineSmall)
                                    IconButton(onClick = onIncrease, enabled = state.canIncrease) { Text("+") }
                                }
                                Text("Valor da cota: R$ ${detail.sharePriceWithoutFee}")
                                Text("Tarifa: R$ ${detail.lastShareFee}")
                                Text("Total da primeira cota: R$ ${detail.lastSharePriceWithFee}", fontWeight = FontWeight.Bold)
                                state.addToCartError?.let { error ->
                                    Text(error, color = CaixaDS.colors.error)
                                }
                                CaixaDSLegacyPrimaryButton(
                                    text = if (state.isAddingToCart) "Adicionando..." else "Adicionar ao carrinho",
                                    onClick = onAddToCart,
                                    enabled = state.selectedShares > 0 && !state.isAddingToCart,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun BetCard(position: Int, bet: MarketplaceBet) {
    CaixaDSLegacyCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Aposta $position", color = CaixaDS.colors.brand, style = CaixaDS.typography.titleMedium)
            if (bet.surprise) Text("Surpresinha")
            bet.numbers.chunked(8).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { number ->
                        CaixaDSNumberBall(number)
                    }
                }
            }
            if (bet.clovers.isNotEmpty()) Text("Trevos: ${bet.clovers.joinToString(" • ")}")
        }
    }
}

@Composable
private fun DetailErrorContent(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    CaixaDSLegacyEmptyState(
        title = "Não foi possível carregar o bolão",
        message = message,
        actionLabel = "Tentar novamente",
        onAction = onRetry,
        modifier = modifier,
    )
}
