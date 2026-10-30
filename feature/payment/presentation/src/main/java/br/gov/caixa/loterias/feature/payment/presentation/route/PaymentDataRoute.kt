package br.gov.caixa.loterias.feature.payment.presentation.route

import androidx.compose.ui.res.stringResource

import br.gov.caixa.loterias.feature.payment.R as LocalizedR

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentDataActions
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentDataContent
import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.payment.presentation.mapper.secondsUntil
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PIX_PENDING_EXIT_MESSAGE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.prePurchaseMessage
import br.gov.caixa.loterias.feature.payment.presentation.mapper.paymentOperatingHours
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentViewModel
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseNoticeDialog
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseRedirectEffect
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseRedirectResumeEffect

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
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
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
    LaunchedEffect(state.pixApproved) { if (state.pixApproved) onApproved() }
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
    if (state.showPrePurchaseConfirmation) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = prePurchaseMessage(formatBrazilianCurrency(state.cartTotal)),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
            onPositive = viewModel::acceptPrePurchase,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = onBack,
            onDismiss = onBack
        )
    }
    state.deleteCandidate?.let { card ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_excluir_cartao_84b14c),
            message = uiStrings.getString(LocalizedR.string.ui_voce_tem_certeza_de_que_deseja_exclu_693a88, card.brand, card.lastDigits),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = viewModel::confirmDelete,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::cancelDelete
        )
    }
    state.pendingAddMethodId?.let { methodId ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_o_cartao_de_credito_adicionado_deve_3f3833),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = {
                viewModel.cancelAddCard()
                onAddCard(methodId)
            }
        )
    }
    if (state.pixRejected) {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_pagamento_nao_aprovado_b8c8b6),
            uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_confirmar_o_pagamen_baf7ad),
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissPixStatus
        )
    }
    if (showPixExitConfirmation) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = PIX_PENDING_EXIT_MESSAGE,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = { showPixExitConfirmation = false },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_voltar_ao_inicio_372787),
            onNegative = onHome
        )
    }
    state.damMessage?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            paymentOperatingHours(it),
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissDamMessage
        )
    }
    state.errorMessage?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissError
        )
    }
}
