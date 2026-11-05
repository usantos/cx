package br.gov.caixa.loterias.feature.cart.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.domain.contract.account.model.PostLoginDestination
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.components.ServiceFailureOverlay
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.presentation.components.CartLocationDialogs
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartActions
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartConfirmationDialogs
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartDialogActions
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartScaffold
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartContent
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import kotlinx.coroutines.delay

@Composable
fun CurrentCartRoute(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onContinueBetting: () -> Unit,
    onPayment: () -> Unit,
    onLogin: () -> Unit,
    onAccountRequired: (PostLoginDestination) -> Unit,
    onLocationBlocked: () -> Unit,
    onValidationRedirect: (ServiceRedirect) -> Unit,
    onPoolDetail: (String) -> Unit = {},
    onComboDetail: (Long) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cart = state.cart
    BackHandler(onBack = onBack)
    ServiceFailureOverlay(
        state.validationFailure,
        viewModel::acceptValidationNotice,
        viewModel::dismissValidationNotice,
        viewModel::consumeValidationRedirect,
        onValidationRedirect
    )
    CartLocationDialogs(state, viewModel, onLocationBlocked)
    LaunchedEffect(state.paymentAccountDecision) {
        val decision = state.paymentAccountDecision
        if (decision is CartPaymentAccountDecision.CompleteAccount) {
            viewModel.consumePaymentAccountDecision()
            onAccountRequired(decision.destination)
        }
    }
    LaunchedEffect(state.paymentReady) {
        if (state.paymentReady) {
            viewModel.consumePaymentReady()
            onPayment()
        }
    }
    CurrentCartScaffold(
        state = state,
        cart = cart,
        onBack = onBack,
        onContinueBetting = onContinueBetting,
        actions = CurrentCartActions(
            requestClear = viewModel::requestClear,
            requestFavorite = viewModel::requestFavorite,
            requestPayment = viewModel::requestPayment,
            load = viewModel::load
        )
    ) { padding ->
        CartContent(state, padding, viewModel, onPoolDetail, onComboDetail)
    }
    CurrentCartConfirmationDialogs(
        state,
        CurrentCartDialogActions(
            reviewRecentPurchase = viewModel::reviewRecentPurchase,
            continueRecentPurchase = viewModel::continueRecentPurchase,
            consumePaymentAccountDecision = viewModel::consumePaymentAccountDecision,
            confirmRemoval = viewModel::confirmRemoval,
            dismissConfirmation = viewModel::dismissConfirmation,
            confirmClear = viewModel::confirmClear,
            updateFavoriteName = viewModel::updateFavoriteName,
            submitFavoriteName = viewModel::submitFavoriteName,
            confirmFavoriteOverwrite = viewModel::confirmFavoriteOverwrite,
            login = {
                viewModel.consumePaymentAccountDecision()
                onLogin()
            }
        )
    )
}

@Composable
private fun CartContent(
    state: CartUiState,
    padding: PaddingValues,
    viewModel: CartViewModel,
    onPoolDetail: (String) -> Unit,
    onComboDetail: (Long) -> Unit,
) {
    val items = state.cart?.items.orEmpty()
    var nowMillis by remember(viewModel) { mutableLongStateOf(viewModel.reservationNow().toEpochMilli()) }
    val hasExpiringShare = items.any { it.poolShare?.expiresAt?.isNotBlank() == true }
    LaunchedEffect(hasExpiringShare) {
        while (hasExpiringShare) {
            nowMillis = viewModel.reservationNow().toEpochMilli()
            viewModel.refreshExpiredReservations()
            delay(1_000)
        }
    }
    CurrentCartContent(
        state = state,
        padding = padding,
        items = items,
        nowMillis = nowMillis,
        onRemove = viewModel::requestRemoval,
        onPoolDetail = onPoolDetail,
        onComboDetail = onComboDetail
    )
}
