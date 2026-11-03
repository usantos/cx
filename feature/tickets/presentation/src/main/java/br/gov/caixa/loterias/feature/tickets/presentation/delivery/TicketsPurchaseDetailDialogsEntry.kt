package br.gov.caixa.loterias.feature.tickets.presentation.delivery

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.tickets.presentation.components.FavoritePurchaseActions
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.TicketsPixNoticeUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

interface TicketsPurchaseDetailDialogsEntry {
    @Composable
    fun PurchaseFavoriteDialogs(
        state: TicketsUiState,
        actions: FavoritePurchaseActions,
        onConfirmOverwrite: () -> Unit,
        onCancelOverwrite: () -> Unit,
        onFavoriteSaved: () -> Unit,
        onDismissDam: () -> Unit,
    )

    @Composable
    fun PurchasePixNotice(
        state: TicketsUiState,
        onConsumeNotice: (br.gov.caixa.loterias.feature.tickets.presentation.mapper.TicketsPixNoticeUiState, Boolean) -> Boolean,
        onOpen: (String) -> Unit,
        onPixRegistration: (() -> Unit)?,
    )
}
