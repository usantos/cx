package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import java.math.BigDecimal

@Composable
fun ConfirmedBetsContent(
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
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.ConfirmedBetsContent(
            state,
            onBack,
            onOpen,
            onAddToCart,
            onBetNow,
            onHelp,
            cartTotal,
            cartBetCount,
            onCart,
            privateDataEnabled,
            actions
        )
    }
}
