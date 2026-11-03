package br.gov.caixa.loterias.feature.payment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.navigation.state.PurchaseRecoveryEvidence
import br.gov.caixa.loterias.core.navigation.state.PurchaseRecoverySession
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentCardViewModel
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentViewModel

internal fun recoveryAccess(session: PurchaseRecoverySession?): FeatureEntryAccess =
    if (session?.current() != null) FeatureEntryAccess.CONTINUATION else FeatureEntryAccess.OPTIONAL

internal fun PaymentViewModel.recoveryEvidence(): PurchaseRecoveryEvidence? {
    val current = state.value
    if (current.pixSessionMismatch) return null
    val id = current.pix?.purchaseId?.takeIf { it > 0 }
    return when {
        current.pixApproved && id != null -> PurchaseRecoveryEvidence.Confirmed(id)
        id != null -> PurchaseRecoveryEvidence.Review(id)
        current.pixRecoveryRequired -> PurchaseRecoveryEvidence.Review(null)
        else -> null
    }
}

internal fun PaymentCardViewModel.recoveryEvidence(): PurchaseRecoveryEvidence? {
    val current = state.value
    val id = recoveryPurchaseId
    return when {
        current.stage == PaymentCardStage.SUCCESS && id != null -> PurchaseRecoveryEvidence.Confirmed(id)
        current.purchaseReview != null -> PurchaseRecoveryEvidence.Review(current.purchaseReview.purchaseId?.takeIf { it > 0 })
        current.trackingPurchase && id != null -> PurchaseRecoveryEvidence.Review(id)
        else -> null
    }
}

@Composable
internal fun ObservePixRecovery(viewModel: PaymentViewModel, recovery: PurchaseRecoverySession?) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.pix, state.pixApproved, state.pixRecoveryRequired, recovery) {
        viewModel.recoveryEvidence()?.let { recovery?.grant(it) }
    }
}

@Composable
internal fun ObserveCardRecovery(viewModel: PaymentCardViewModel, recovery: PurchaseRecoverySession?) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.stage, state.trackingPurchase, state.purchaseReview, recovery) {
        viewModel.recoveryEvidence()?.let { recovery?.grant(it) }
    }
}
