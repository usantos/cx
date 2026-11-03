package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultVisualData
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry

@Composable
fun TicketResultScreen(
    bets: List<ConfirmedBet>,
    onBack: () -> Unit,
    visualData: TicketResultVisualData? = null,
    onBetNow: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
) {
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = onBack,
        preserveActive = false
    ) { entry ->
        entry.TicketResultScreen(bets, onBack, visualData, onBetNow, onHelp)
    }
}
