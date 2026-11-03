package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.core.presentation.delivery.featureModuleEnabled
import br.gov.caixa.loterias.feature.tickets.presentation.components.FavoritePurchaseActions
import br.gov.caixa.loterias.feature.tickets.presentation.delivery.TicketsUiEntry
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
fun PurchaseFavoriteDialogs(
    state: TicketsUiState,
    actions: FavoritePurchaseActions,
    onConfirmOverwrite: () -> Unit,
    onCancelOverwrite: () -> Unit,
    onFavoriteSaved: () -> Unit,
    onDismissDam: () -> Unit,
) {
    val visible = state.showFavoriteDialog || state.favoriteOverwriteConfirmation || state.favoriteSaved || state.damMessage != null
    if (!visible) return
    val completedOperation = state.selectedPurchase?.id?.let { it > 0 } == true && (state.favoriteSaved || state.damMessage != null)
    if (!featureModuleEnabled(AppModule.TICKETS) && !completedOperation) return
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = actions.closeFavoriteDialog,
        access = if (completedOperation) FeatureEntryAccess.CONTINUATION else FeatureEntryAccess.OPTIONAL,
        preserveActive = completedOperation
    ) { entry ->
        entry.PurchaseFavoriteDialogs(state, actions, onConfirmOverwrite, onCancelOverwrite, onFavoriteSaved, onDismissDam)
    }
}

@Composable
fun PurchasePixNotice(
    state: TicketsUiState,
    onConsumeNotice: (br.gov.caixa.loterias.feature.tickets.presentation.mapper.TicketsPixNoticeUiState, Boolean) -> Boolean,
    onOpen: (String) -> Unit,
    onPixRegistration: (() -> Unit)?,
) {
    if (state.pixPurchaseNotice == null) return
    val continuation = state.selectedPurchase?.id?.let { it > 0 } == true
    FeatureModuleContent(
        module = AppModule.TICKETS,
        entryType = TicketsUiEntry::class.java,
        onBack = { onOpen("home") },
        access = if (continuation) FeatureEntryAccess.CONTINUATION else FeatureEntryAccess.OPTIONAL,
        preserveActive = continuation
    ) { entry ->
        entry.PurchasePixNotice(state, onConsumeNotice, onOpen, onPixRegistration)
    }
}
