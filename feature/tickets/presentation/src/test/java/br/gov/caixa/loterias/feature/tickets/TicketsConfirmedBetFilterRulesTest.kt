package br.gov.caixa.loterias.feature.tickets

import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import br.gov.caixa.loterias.feature.tickets.navigation.PURCHASE_HELP_ROUTE
import br.gov.caixa.loterias.feature.tickets.presentation.coordinator.TicketsHistoryBetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.withAvailableHelp
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetFilters
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseDialogAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseStatusDialog
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsStateAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.reduceTicketsState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TicketsConfirmedBetFilterRulesTest {
    @Test fun `clearing filters preserves history period and restores configured defaults before reloading`() {
        val month = TicketPeriod(HISTORY_MONTH, HISTORY_YEAR, "Setembro")
        val config = ConfirmedBetsConfiguration(
            defaultPeriod = TicketPeriod(RECENT_PERIOD, description = "Últimos 45 dias"),
            defaultStatus = DEFAULT_STATUS,
            defaultOrdering = 2,
            betTypes = listOf(TicketFilterOption(DEFAULT_BET_TYPE, "Todas")),
            contestTypes = listOf(TicketFilterOption(DEFAULT_CONTEST_TYPE, "Todos"))
        )
        val state = reduxStateStore<TicketsUiState, TicketsStateAction>(
            TicketsUiState(
                betConfiguration = config,
                betFilters = BetFilters(month, 2, CHANGED_ORDERING, 2, 1, SELECTED_MODALITY, true, true, true),
                showBetFilters = true
            ),
            ::reduceTicketsState
        )
        var applied: BetFilters? = null
        val coordinator = TicketsHistoryBetFilters(state) { applied = state.value.betFilters }
        coordinator.clearBetFilters()

        assertEquals(BetFilters(month, DEFAULT_STATUS, 2, DEFAULT_BET_TYPE, DEFAULT_CONTEST_TYPE), state.value.betFilters)
        assertEquals(state.value.betFilters, applied)
        assertEquals(0, state.value.betFilters.activeCount(config))
        assertFalse(state.value.showBetFilters)
    }

    @Test fun `purchase dialog omits Help without a supported destination`() {
        val dialog = PurchaseStatusDialog(
            "Atenção",
            "Compra cancelada",
            listOf(
                PurchaseDialogAction.SAVE_FAVORITE to "Salvar Carrinho",
                PurchaseDialogAction.HELP to "Ajuda",
                PurchaseDialogAction.DISMISS to "Cancelar"
            )
        )
        assertEquals(
            listOf(PurchaseDialogAction.SAVE_FAVORITE, PurchaseDialogAction.DISMISS),
            dialog.withAvailableHelp(false).actions.map { it.first }
        )
        assertEquals(dialog, dialog.withAvailableHelp(true))
    }

    @Test fun `purchase help uses the registered FAQ tab destination`() {
        assertEquals("support/terms?tab=FAQ", PURCHASE_HELP_ROUTE)
    }
}

private const val HISTORY_MONTH = 9
private const val HISTORY_YEAR = 2026
private const val RECENT_PERIOD = 45
private const val DEFAULT_BET_TYPE = 7L
private const val DEFAULT_CONTEST_TYPE = 9L
private const val SELECTED_MODALITY = 4
private const val DEFAULT_STATUS = 3L
private const val CHANGED_ORDERING = 3L
