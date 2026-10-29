package br.gov.caixa.loterias.feature.payment.presentation.route

import androidx.compose.ui.res.stringResource

import br.gov.caixa.loterias.feature.payment.R as LocalizedR

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseRedirect
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentCardActions
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentCardFlowScreen
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentCardViewModel
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PURCHASE_REVIEW_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseNoticeDialog
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseRedirectEffect
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseRedirectResumeEffect

@Composable
fun PaymentCardFlowRoute(
    viewModel: PaymentCardViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onReviewPurchases: () -> Unit,
    onRedirect: (PurchaseRedirect) -> Unit,
    onMoreBets: () -> Unit,
    onRegistration: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    PurchaseRedirectResumeEffect(viewModel::resumeAfterRedirect)
    LaunchedEffect(state.stage) { if (state.stage == PaymentCardStage.SUCCESS) onSuccess() }
    PaymentCardFlowScreen(
        state = state,
        onBack = onBack,
        actions = PaymentCardActions(
            changeNumber = viewModel::changeNumber,
            changeHolderName = viewModel::changeHolderName,
            changeMonth = viewModel::changeMonth,
            changeYear = viewModel::changeYear,
            toggleSave = viewModel::toggleSave,
            continueFromCard = viewModel::continueFromCard,
            changeCvv = viewModel::changeCvv,
            continueFromCvv = viewModel::continueFromCvv,
            submit = viewModel::submit,
            retry = viewModel::retry
        )
    )
    PurchaseRedirectEffect(state.redirect, viewModel::consumeRedirect, onRedirect)
    PurchaseNoticeDialog(state.notice, viewModel::consumeNotice, onMoreBets) {
        if (viewModel.consumeRegistrationNotice(it)) onRegistration()
    }
    if (state.stage == PaymentCardStage.REVIEW_REQUIRED && !state.redirectPaused) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = PURCHASE_REVIEW_GUIDANCE,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { if (viewModel.acknowledgePurchaseReview()) onReviewPurchases() }
        )
    }
    state.errorMessage?.takeIf { state.stage != PaymentCardStage.FAILURE }?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissError
        )
    }
}
