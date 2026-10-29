package br.gov.caixa.loterias.feature.cart.presentation.route

import br.gov.caixa.loterias.feature.cart.R as LocalizedR

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
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.components.ServiceFailureOverlay
import kotlinx.coroutines.delay
import br.gov.caixa.loterias.feature.cart.presentation.components.CartLocationDialogs
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import br.gov.caixa.loterias.feature.cart.presentation.viewmodel.CartViewModel
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartContent
import br.gov.caixa.loterias.feature.cart.presentation.components.ConfirmCartDialog
import br.gov.caixa.loterias.feature.cart.presentation.components.FavoriteNameDialog
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartActions
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartScaffold

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
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
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
    if (state.recentPurchaseConfirmation) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_existe_compra_efetivada_em_processam_8900c1),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = viewModel::reviewRecentPurchase,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::continueRecentPurchase,
            onDismiss = viewModel::reviewRecentPurchase
        )
    }
    when (state.paymentAccountDecision) {
        CartPaymentAccountDecision.LoginRequired -> CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_para_continuar_utilizando_a_funciona_a5583f),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = {
                viewModel.consumePaymentAccountDecision()
                onLogin()
            },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::consumePaymentAccountDecision,
            onDismiss = viewModel::consumePaymentAccountDecision
        )

        CartPaymentAccountDecision.Suspended -> CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_ha_uma_suspensao_temporaria_ativa_pa_a64d6f),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = viewModel::consumePaymentAccountDecision,
            onDismiss = viewModel::consumePaymentAccountDecision
        )

        else -> Unit
    }
    state.pendingRemoval?.let {
        ConfirmCartDialog(
            message = uiStrings.getString(LocalizedR.string.ui_deseja_excluir_esta_aposta_do_carrin_dfea3f),
            onConfirm = viewModel::confirmRemoval,
            onDismiss = viewModel::dismissConfirmation
        )
    }
    if (state.clearConfirmation) {
        ConfirmCartDialog(
            message = uiStrings.getString(LocalizedR.string.ui_deseja_excluir_todas_as_apostas_do_c_57359b),
            onConfirm = viewModel::confirmClear,
            onDismiss = viewModel::dismissConfirmation
        )
    }
    if (state.favoriteNameDialog) {
        FavoriteNameDialog(state, viewModel::updateFavoriteName, viewModel::submitFavoriteName, viewModel::dismissConfirmation)
    }
    if (state.favoriteOverwriteConfirmation) {
        ConfirmCartDialog(
            message = uiStrings.getString(LocalizedR.string.ui_ja_existe_um_carrinho_favorito_com_e_c68666),
            onConfirm = viewModel::confirmFavoriteOverwrite,
            onDismiss = viewModel::dismissConfirmation
        )
    }
    if (state.favoriteSaved) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_sucesso_29bf2d),
            message = uiStrings.getString(LocalizedR.string.ui_carrinho_salvo_nos_favoritos_349648),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = viewModel::dismissConfirmation
        )
    }
    // Esta janela deve ficar acima dos diálogos de confirmação e nome, como na sobreposição bloqueante de requisição da CX.
    if (state.loading || state.loadingParameters || state.expirationRemovalInFlight) {
        br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog()
    }
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
