package br.gov.caixa.loterias.feature.tickets.presentation.route

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.BetFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.FavoritePurchaseActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.FavoritePurchaseDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.LegacyTicketResultContent
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseBetFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseFiltersDialog
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseStatusDialogView
import br.gov.caixa.loterias.feature.tickets.presentation.components.TicketResultVisualData
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.withAvailableHelp
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetsContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetsListActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ManualTicketContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailActions
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchaseDetailContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasesContent
import br.gov.caixa.loterias.feature.tickets.presentation.screen.PurchasesListActions
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseDialogAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel
import java.math.BigDecimal

@Composable
fun ConfirmedBetsDataRoute(
    viewModel: TicketsViewModel,
    onBack: () -> Unit,
    onOpen: (ConfirmedBet) -> Unit,
    onAddToCart: ((ConfirmedBet) -> Unit)? = null,
    onBetNow: (() -> Unit)? = null,
    onHelp: (() -> Unit)? = null,
    cartTotal: BigDecimal = BigDecimal.ZERO,
    cartBetCount: Int = 0,
    onCart: (() -> Unit)? = null,
    privateDataEnabled: Boolean = true,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel, privateDataEnabled) { if (privateDataEnabled && !state.loaded) viewModel.loadBets() }
    ConfirmedBetsContent(
        state = state,
        onBack = onBack,
        onOpen = onOpen,
        onAddToCart = onAddToCart,
        onBetNow = onBetNow,
        onHelp = onHelp,
        cartTotal = cartTotal,
        cartBetCount = cartBetCount,
        onCart = onCart,
        privateDataEnabled = privateDataEnabled,
        actions = ConfirmedBetsListActions(
            openBetFilters = viewModel::openBetFilters,
            retryBets = viewModel::retryBets,
            selectPeriod = {
                viewModel.selectBetPeriod(it)
                viewModel.applyBetFilters()
            }
        )
    )
    if (state.showBetFilters) {
        BetFiltersDialog(
            state,
            BetFiltersActions(
                closeBetFilters = viewModel::closeBetFilters,
                selectBetPeriod = viewModel::selectBetPeriod,
                selectBetStatus = viewModel::selectBetStatus,
                selectBetOrdering = viewModel::selectBetOrdering,
                selectBetType = viewModel::selectBetType,
                selectContestType = viewModel::selectContestType,
                selectModality = viewModel::selectModality,
                toggleSurprise = viewModel::toggleSurprise,
                toggleRecurring = viewModel::toggleRecurring,
                toggleCombo = viewModel::toggleCombo,
                applyBetFilters = viewModel::applyBetFilters,
                clearBetFilters = viewModel::clearBetFilters
            )
        )
    }
}

@Composable
fun PurchasesDataRoute(viewModel: TicketsViewModel, onBack: () -> Unit, onOpen: (Purchase) -> Unit, privateDataEnabled: Boolean = true) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel, privateDataEnabled) { if (privateDataEnabled && !state.loaded) viewModel.loadPurchases() }
    PurchasesContent(
        state = state,
        onBack = onBack,
        onOpen = onOpen,
        actions = PurchasesListActions(
            openPurchaseFilters = viewModel::openPurchaseFilters,
            selectPurchaseMonth = viewModel::selectPurchaseMonth,
            retryPurchases = viewModel::retryPurchases,
            loadMorePurchases = viewModel::loadMorePurchases
        )
    )
    if (state.showPurchaseFilters) {
        PurchaseFiltersDialog(
            state,
            PurchaseFiltersActions(
                closePurchaseFilters = viewModel::closePurchaseFilters,
                selectPaymentMethod = viewModel::selectPaymentMethod,
                selectPurchaseStatus = viewModel::selectPurchaseStatus,
                applyPurchaseFilters = viewModel::applyPurchaseFilters,
                clearPurchaseFilters = viewModel::clearPurchaseFilters
            )
        )
    }
}

@Composable
fun PurchaseDetailDataRoute(
    viewModel: TicketsViewModel,
    purchase: Purchase,
    onBack: () -> Unit,
    onOpen: (String) -> Unit = {},
    onPixRedirect: (br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect) -> Unit = {},
    onPixRegistration: (() -> Unit)? = null,
    onHelp: (() -> Unit)? = null,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    br.gov.caixa.loterias.core.presentation.effect.NavigationResumeEffect(viewModel::resumePixPurchaseAfterRedirect)
    LaunchedEffect(state.pixPurchaseRedirect) {
        state.pixPurchaseRedirect?.let { if (viewModel.consumePixPurchaseRedirect(it)) onPixRedirect(it) }
    }
    PurchasePixNotice(state, viewModel, onOpen, onPixRegistration)
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
    PurchaseFavoriteDialogs(state, viewModel, onOpen)
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

@Composable
private fun PurchaseFavoriteDialogs(state: TicketsUiState, viewModel: TicketsViewModel, onOpen: (String) -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.showFavoriteDialog) {
        FavoritePurchaseDialog(
            state,
            FavoritePurchaseActions(
                closeFavoriteDialog = viewModel::closeFavoriteDialog,
                changeFavoriteName = viewModel::changeFavoriteName,
                toggleKeepSurprises = viewModel::toggleKeepSurprises,
                validateAndSaveFavorite = viewModel::validateAndSaveFavorite
            )
        )
    }
    if (state.favoriteOverwriteConfirmation) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_ja_existe_um_carrinho_favorito_com_e_c68666),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = viewModel::confirmFavoriteOverwrite,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::cancelFavoriteOverwrite
        )
    }
    if (state.favoriteSaved) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_carrinho_favorito_salvo_com_sucesso_441141),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = {
                viewModel.dismissFavoriteSaved()
                onOpen("cart/favorites")
            }
        )
    }
    state.damMessage?.let { message ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = viewModel::dismissDamMessage
        )
    }
}

@Composable
fun ManualTicketDataRoute(
    viewModel: TicketsViewModel,
    onBack: () -> Unit,
    onResult: (List<ConfirmedBet>) -> Unit,
    onHelp: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.loaded, state.bets, state.ticketResult) {
        if (viewModel.consumeTicketResultNavigation()) onResult(state.bets)
    }
    val manualCodeValid = state.ticketCode.length >= TicketsViewModel.MINIMUM_MANUAL_TICKET_LENGTH
    ManualTicketContent(
        state = state,
        onBack = onBack,
        onHelp = onHelp,
        manualCodeValid = manualCodeValid,
        actions = ManualTicketActions(
            changeTicketCode = viewModel::changeTicketCode,
            changeTicketRetailerCode = viewModel::changeTicketRetailerCode,
            checkTicket = { viewModel.checkTicket() }
        )
    )
}

@Composable
fun TicketResultDataRoute(
    bets: List<ConfirmedBet>,
    onBack: () -> Unit,
    visualData: TicketResultVisualData? = null,
    onBetNow: (() -> Unit)? = null,
    onHelp: () -> Unit = {},
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_resultado_da_aposta_4e6334),
        onBack = onBack,
        containerColor = CaixaDSColorTokens.White,
        topBarAction = {
            IconButton(onClick = onHelp, modifier = Modifier.uiId("tickets.0152")) {
                Image(
                    painterResource(br.gov.caixa.loterias.feature.tickets.R.drawable.legacy_tickets_icon_duvida2),
                    uiStrings.getString(LocalizedR.string.ui_duvidas_10e026),
                    Modifier.size(24.dp)
                )
            }
        },
        modifier = Modifier.uiId("tickets.0153")
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LegacyTicketResultContent(bets, visualData, onBetNow)
        }
    }
}

@Composable
private fun PurchasePixNotice(
    state: TicketsUiState,
    viewModel: TicketsViewModel,
    onOpen: (String) -> Unit,
    onPixRegistration: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    state.pixPurchaseNotice?.let { notice ->
        val registration = notice.offerRegistration && onPixRegistration != null
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = notice.message,
            positiveLabel = when {
                registration -> uiStrings.getString(LocalizedR.string.ui_sim_931345)
                notice.offerMoreBets -> uiStrings.getString(LocalizedR.string.ui_apostar_mais_a6282e)
                else -> uiStrings.getString(LocalizedR.string.ui_entendi_9f9373)
            },
            onPositive = {
                if (viewModel.consumePixPurchaseNotice(notice, notice.offerMoreBets || registration)) {
                    if (registration) {
                        onPixRegistration.invoke()
                    } else if (notice.offerMoreBets) {
                        onOpen("home")
                    }
                }
            },
            negativeLabel = when {
                registration -> uiStrings.getString(LocalizedR.string.ui_nao_9bc947)
                notice.offerMoreBets -> uiStrings.getString(LocalizedR.string.ui_entendi_9f9373)
                else -> null
            },
            onNegative = { viewModel.consumePixPurchaseNotice(notice) }
        )
    }
}
