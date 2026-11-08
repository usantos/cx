package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.cart.LoteriasCaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.ErrorData
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyConfirmedBetRow
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyConfirmedBetsIntro
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyEmptyBets
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketPeriodSelector
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketsFilterAction
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketsHelpAction
import br.gov.caixa.loterias.feature.tickets.presentation.components.LoadingData
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR
import java.math.BigDecimal

@Composable
fun ConfirmedBetsContentDelivered(
    state: TicketsUiState,
    onBack: () -> Unit,
    onOpen: (ConfirmedBet) -> Unit,
    onAddToCart: ((ConfirmedBet) -> Unit)?,
    onBetNow: (() -> Unit)?,
    onHelp: (() -> Unit)?,
    cartTotal: BigDecimal,
    cartBetCount: Int,
    onCart: (() -> Unit)?,
    privateDataEnabled: Boolean,
    actions: ConfirmedBetsListActions,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val listState = rememberLazyListState()
    LaunchedEffect(state.betFilters.period) { listState.scrollToItem(0) }
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_minhas_apostas_807108),
        onBack = onBack,

        // Scaffold reserva 48dp por padrão; as duas ações legadas precisam de suas larguras completas.
        topBarActionWidth = if (onHelp != null) 93.dp else 48.dp,
        topBarAction = {
            Row {
                LegacyTicketsFilterAction(state.betFilters.activeCount(state.betConfiguration), actions.openBetFilters)
                onHelp?.let { LegacyTicketsHelpAction(it) }
            }
        },
        bottomBar = { LoteriasCaixaDSCartSummary(cartTotal, cartBetCount, onClick = onCart, modifier = Modifier.uiId("tickets.0163")) },
        containerColor = LoteriasCaixaDSColorTokens.White,
        modifier = Modifier.uiId("tickets.0164")
    ) { padding ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = (Modifier.fillMaxSize().padding(padding)).uiId("tickets.0165"),
            state = listState
        ) {
            item { LegacyConfirmedBetsIntro(state.historyBackend) }
            item {
                LegacyTicketPeriodSelector(
                    uiStrings.getString(DeliveryR.string.ui_selecione_o_periodo_2d4193),
                    state.betFilters.period?.description ?: if (!privateDataEnabled) {
                        uiStrings.getString(
                            DeliveryR.string.ui_ultimos_7_dias_e05d6c
                        )
                    } else {
                        ""
                    },
                    state.betConfiguration?.periods.orEmpty().map { it.description },
                    (Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp)).uiId("tickets.0166")
                ) { index -> state.betConfiguration?.periods?.getOrNull(index)?.let { actions.selectPeriod(it) } }
            }
            item { Spacer(Modifier.height(16.dp)) }
            when {
                state.isLoading -> item { LoadingData((Modifier.height(100.dp)).uiId("tickets.0167")) }

                state.errorMessage != null -> item {
                    ErrorData(
                        state.errorMessage.orEmpty(),
                        actions.retryBets,
                        modifier = Modifier
                            .uiId("tickets.0168")
                    )
                }

                state.loaded && state.bets.isEmpty() -> item { LegacyEmptyBets(onBetNow) }

                else -> items(state.bets.size, key = { state.bets[it].id }) { index ->
                    val bet = state.bets[index]
                    LegacyConfirmedBetRow(bet, { onOpen(bet) }, onAddToCart?.let { { it(bet) } })
                }
            }
            if (privateDataEnabled && !state.isLoading && state.errorMessage == null && state.bets.isNotEmpty()) {
                item(key = "confirmed-bets-pagination") {
                    val pageError = state.betPageErrorMessage
                    when {
                        state.isLoadingMoreBets -> LoadingData(Modifier.height(100.dp).uiId("tickets.0400"))
                        pageError != null -> Box(Modifier.uiId("tickets.0401")) {
                            ErrorData(pageError, actions.loadMoreBets)
                        }
                        state.canLoadMoreBets -> LaunchedEffect(state.bets.size) { actions.loadMoreBets() }
                    }
                }
            }
            item { Spacer(Modifier.height(120.dp)) }
        }
    }
}
