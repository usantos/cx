package br.gov.caixa.loterias.feature.tickets.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.domain.contract.tickets.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.components.FavoritePurchaseActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseStatusDialogView
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.withAvailableHelp
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseFavoriteDialogs
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasePixNotice
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseDialogAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

@Composable
fun PurchaseDetailDataRoute(
    viewModel: TicketsViewModel,
    purchase: Purchase,
    onBack: () -> Unit,
    onOpen: (String) -> Unit = {},
    onPixRedirect: (br.gov.caixa.loterias.core.domain.contract.payment.model.PurchaseRedirect) -> Unit = {},
    onPixRegistration: (() -> Unit)? = null,
    onHelp: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    androidx.compose.runtime.key(viewModel, purchase.id) {
        br.gov.caixa.loterias.core.presentation.effect.ScreenForegroundEffect(viewModel::setPixScreenActive)
    }
    br.gov.caixa.loterias.core.presentation.effect.NavigationResumeEffect(viewModel::resumePixPurchaseAfterRedirect)
    LaunchedEffect(state.pixPurchaseRedirect) {
        state.pixPurchaseRedirect?.let { if (viewModel.consumePixPurchaseRedirect(it)) onPixRedirect(it) }
    }
    PurchasePixNotice(state, viewModel::consumePixPurchaseNotice, onOpen, onPixRegistration)
    LaunchedEffect(purchase.id) { viewModel.loadPurchase(purchase) }
    PurchaseDetailContent(
        state = state,
        purchase = purchase,
        onBack = onBack,
        actions = PurchaseDetailActions(
            openPurchaseBetFilters = viewModel::openPurchaseBetFilters,
            loadPurchase = viewModel::loadPurchase,
            openFavoriteDialog = viewModel::openFavoriteDialog,
            pendingPixSecondsRemaining = viewModel::pendingPixSecondsRemaining,
            checkPendingPixStatus = viewModel::checkPendingPixStatus,
            clearPurchaseBetFilters = viewModel::clearPurchaseBetFilters,
            openHelp = onHelp
        )
    )
    if (state.showPurchaseBetFilters) {
        PurchaseBetFiltersDialog(
            state,
            PurchaseBetFiltersActions(
                closePurchaseBetFilters = viewModel::closePurchaseBetFilters,
                selectPurchaseBetContest = viewModel::selectPurchaseBetContest,
                selectPurchaseBetStatus = viewModel::selectPurchaseBetStatus,
                selectPurchaseBetModality = viewModel::selectPurchaseBetModality,
                applyPurchaseBetFilters = viewModel::applyPurchaseBetFilters,
                clearPurchaseBetFilters = viewModel::clearPurchaseBetFilters
            )
        )
    }
    PurchaseStatusDialogs(state, viewModel, onOpen, onHelp)
    PurchaseFavoriteDialogs(
        state,
        FavoritePurchaseActions(
            closeFavoriteDialog = viewModel::closeFavoriteDialog,
            changeFavoriteName = viewModel::changeFavoriteName,
            toggleKeepSurprises = viewModel::toggleKeepSurprises,
            validateAndSaveFavorite = viewModel::validateAndSaveFavorite
        ),
        onConfirmOverwrite = viewModel::confirmFavoriteOverwrite,
        onCancelOverwrite = viewModel::cancelFavoriteOverwrite,
        onFavoriteSaved = {
            viewModel.dismissFavoriteSaved()
            onOpen("cart/favorites")
        },
        onDismissDam = viewModel::dismissDamMessage
    )
}

@Composable
private fun PurchaseStatusDialogs(state: TicketsUiState, viewModel: TicketsViewModel, onOpen: (String) -> Unit, onHelp: (() -> Unit)?) {
    state.purchaseStatusDialog?.let { dialog ->
        PurchaseStatusDialogView(dialog.withAvailableHelp(onHelp != null)) { action ->
            when (action) {
                PurchaseDialogAction.SAVE_FAVORITE -> viewModel.openFavoriteDialog()

                PurchaseDialogAction.HELP -> {
                    viewModel.dismissPurchaseStatusDialog()
                    onHelp?.invoke()
                }

                PurchaseDialogAction.DISMISS -> viewModel.dismissPurchaseStatusDialog()

                PurchaseDialogAction.NEW_PIX -> {
                    viewModel.dismissPurchaseStatusDialog()
                    onOpen("payment/pix-guidance")
                }

                PurchaseDialogAction.CHANGE_PAYMENT -> {
                    viewModel.dismissPurchaseStatusDialog()
                    onOpen("payment/method")
                }

                PurchaseDialogAction.HOME -> {
                    viewModel.dismissPurchaseStatusDialog()
                    onOpen("home")
                }
            }
        }
    }
}
