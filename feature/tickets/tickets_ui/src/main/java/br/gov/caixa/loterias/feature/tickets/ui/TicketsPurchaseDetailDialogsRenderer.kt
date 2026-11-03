package br.gov.caixa.loterias.feature.tickets.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.presentation.components.FavoritePurchaseActions
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsPurchaseDetailDialogsEntry
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.TicketsPixNoticeUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

internal class TicketsPurchaseDetailDialogsRenderer : TicketsPurchaseDetailDialogsEntry {
    @Composable
    override fun PurchaseFavoriteDialogs(
        state: TicketsUiState,
        actions: FavoritePurchaseActions,
        onConfirmOverwrite: () -> Unit,
        onCancelOverwrite: () -> Unit,
        onFavoriteSaved: () -> Unit,
        onDismissDam: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseFavoriteDialogsDelivered(
            state,
            actions,
            onConfirmOverwrite,
            onCancelOverwrite,
            onFavoriteSaved,
            onDismissDam
        )
    }

    @Composable
    override fun PurchasePixNotice(
        state: TicketsUiState,
        onConsumeNotice: (br.gov.caixa.loterias.feature.tickets.presentation.mapper.TicketsPixNoticeUiState, Boolean) -> Boolean,
        onOpen: (String) -> Unit,
        onPixRegistration: (() -> Unit)?,
    ) {
        br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasePixNoticeDelivered(
            state,
            onConsumeNotice,
            onOpen,
            onPixRegistration
        )
    }
}
