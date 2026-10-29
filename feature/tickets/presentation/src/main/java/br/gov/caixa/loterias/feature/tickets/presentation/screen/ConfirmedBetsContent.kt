package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.presentation.components.ErrorData
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyConfirmedBetRow
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyConfirmedBetsIntro
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyEmptyBets
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketPeriodSelector
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketsFilterAction
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketsHelpAction
import br.gov.caixa.loterias.feature.tickets.presentation.components.LoadingData
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import java.math.BigDecimal

@Composable
internal fun ConfirmedBetsContent(
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
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_minhas_apostas_807108),
        onBack = onBack,

        // Scaffold reserva 48dp por padrão; as duas ações legadas precisam de suas larguras completas.
        topBarActionWidth = if (onHelp != null) 93.dp else 48.dp,
        topBarAction = {
            Row {
                LegacyTicketsFilterAction(state.betFilters.activeCount(state.betConfiguration), actions.openBetFilters)
                onHelp?.let { LegacyTicketsHelpAction(it) }
            }
        },
        bottomBar = { CaixaDSCartSummary(cartTotal, cartBetCount, onClick = onCart, modifier = Modifier.uiId("tickets.0163")) },
        containerColor = CaixaDSColorTokens.White,
        modifier = Modifier.uiId("tickets.0164")
    ) { padding ->
        androidx.compose.foundation.lazy.LazyColumn((Modifier.fillMaxSize().padding(padding)).uiId("tickets.0165")) {
            item { LegacyConfirmedBetsIntro(state.historyBackend) }
            item {
                LegacyTicketPeriodSelector(
                    uiStrings.getString(LocalizedR.string.ui_selecione_o_periodo_2d4193),
                    state.betFilters.period?.description ?: if (!privateDataEnabled) {
                        uiStrings.getString(
                            LocalizedR.string.ui_ultimos_7_dias_e05d6c
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
            item { Spacer(Modifier.height(120.dp)) }
        }
    }
}

internal data class ConfirmedBetsListActions(
    val openBetFilters: () -> Unit,
    val retryBets: () -> Unit,
    val selectPeriod: (TicketPeriod) -> Unit,
)
