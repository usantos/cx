package br.gov.caixa.loterias.feature.payment.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.components.PaymentConfirmationDialogs
import br.gov.caixa.loterias.feature.payment.presentation.components.PaymentStatusDialogs
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseNoticeDialog
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseRedirectEffect
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseRedirectResumeEffect
import br.gov.caixa.loterias.feature.payment.presentation.mapper.secondsUntil
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentDataActions
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentDataContent
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentViewModel

data class PaymentVisualAssets(val recargaPayLogoResourceId: Int = 0)

@Composable
fun PaymentDataRoute(
    mode: PaymentMode,
    viewModel: PaymentViewModel,
    visualAssets: PaymentVisualAssets = PaymentVisualAssets(),
    onBack: () -> Unit,
    onHelp: () -> Unit,
    onPix: () -> Unit,
    onAddCard: (Long) -> Unit,
    onOpenCard: (SavedPaymentCard) -> Unit,
    onApproved: () -> Unit,
    onHome: () -> Unit,
    onPurchases: () -> Unit,
    onRedirect: (br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect) -> Unit,
    onRegistration: () -> Unit,
    privateDataEnabled: Boolean = true,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (mode == PaymentMode.PIX) {
        androidx.compose.runtime.key(viewModel) {
            br.gov.caixa.loterias.core.presentation.effect.ScreenForegroundEffect(viewModel::setPixScreenActive)
        }
    }
    PurchaseRedirectResumeEffect(viewModel::resumeAfterRedirect)
    PurchaseRedirectEffect(state.redirect, viewModel::consumeRedirect, onRedirect)
    PurchaseNoticeDialog(state.notice, viewModel::consumeNotice, onHome) {
        if (viewModel.consumeRegistrationNotice(it)) onRegistration()
    }
    var showPixExitConfirmation by remember { mutableStateOf(false) }
    val handleBack: () -> Unit = {
        if (mode != PaymentMode.PIX) {
            onBack()
        } else if (state.pix?.let { secondsUntil(it.expiresAt) > 0 } == true) {
            showPixExitConfirmation = true
        } else {
            onHome()
        }
    }
    BackHandler(enabled = mode == PaymentMode.PIX, onBack = handleBack)
    LaunchedEffect(state.pixApproved) {
        if (viewModel.consumePixApprovalNavigation()) onApproved()
    }
    PaymentDataContent(
        mode = mode,
        state = state,
        visualAssets = visualAssets,
        handleBack = handleBack,
        onHelp = onHelp,
        onPix = onPix,
        onOpenCard = onOpenCard,
        onPurchases = onPurchases,
        privateDataEnabled = privateDataEnabled,
        actions = PaymentDataActions(
            requestAddCard = viewModel::requestAddCard,
            requestDelete = viewModel::requestDelete,
            checkPixStatus = viewModel::checkPixStatus
        )
    )
    PaymentConfirmationDialogs(
        state = state,
        onBack = onBack,
        onAcceptPrePurchase = viewModel::acceptPrePurchase,
        onConfirmDelete = viewModel::confirmDelete,
        onCancelDelete = viewModel::cancelDelete,
        onConfirmAddCard = { methodId ->
            viewModel.cancelAddCard()
            onAddCard(methodId)
        }
    )
    PaymentStatusDialogs(
        state = state,
        showPixExitConfirmation = showPixExitConfirmation,
        onKeepPix = { showPixExitConfirmation = false },
        onHome = onHome,
        onDismissPixStatus = viewModel::dismissPixStatus,
        onDismissDamMessage = viewModel::dismissDamMessage,
        onDismissError = viewModel::dismissError
    )
}
