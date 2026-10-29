package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.ui.res.stringResource

import br.gov.caixa.loterias.feature.home.R as LocalizedR

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState

internal data class HomeDialogActions(
    val acknowledgeLocalCartReview: () -> Unit,
    val acknowledgeUnidentifiedPayment: () -> Boolean,
    val closeNotification: () -> Unit,
    val dismissNotificationError: () -> Unit,
    val acceptNotificationNotice: (ServiceNoticeUiState) -> Unit,
    val dismissNotificationNotice: (ServiceNoticeUiState) -> Unit,
    val dismissBetEntryError: () -> Unit,
    val openNotificationLink: () -> Unit,
)

/** A prioridade e as proteções dos modais são preservadas; ações são emitidas para HomeRoute. */
@Composable
internal fun HomeRouteDialogs(
    homeState: HomeUiState,
    lifecycleState: Lifecycle.State,
    onOpen: (String) -> Unit,
    actions: HomeDialogActions,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val showLocalCartReview = homeState.localCartReviewRequired && !homeState.localCartReviewAcknowledged &&
        !homeState.loading && !homeState.preparingBet && !homeState.parametersUnavailable
    if (showLocalCartReview) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_ha_apostas_locais_sem_confirmacao_de_e4755e),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_conferir_apostas_f4de2d),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_depois_b8db71),
            onPositive = {
                actions.acknowledgeLocalCartReview()
                onOpen("cart/local-review")
            },
            onNegative = { actions.acknowledgeLocalCartReview() }
        )
    }
    val showPaymentNotice = !showLocalCartReview && homeState.unidentifiedPayment && !homeState.paymentNoticeAcknowledged &&
        !homeState.loading && !homeState.parametersUnavailable && !homeState.preparingBet
    if (showPaymentNotice) {
        br.gov.caixa.loterias.feature.home.presentation.components.HomePaymentNotice {
            if (actions.acknowledgeUnidentifiedPayment()) onOpen("tickets/purchases")
        }
    }
    val showNotification = !showLocalCartReview && lifecycleState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED) &&
        homeState.latestNotification != null && !homeState.unidentifiedPayment && !homeState.notificationDismissed &&
        homeState.notificationReadError == null &&
        !homeState.loading && !homeState.parametersUnavailable && !homeState.preparingBet
    if (showNotification) {
        homeState.latestNotification.let { notification ->
            br.gov.caixa.loterias.feature.home.presentation.components.HomeNotificationPopup(notification, actions.closeNotification) {
                actions.openNotificationLink()
            }
        }
    }
    homeState.notificationReadError?.let {
        br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            actions.dismissNotificationError
        )
    }
    homeState.notificationNotice?.let { notice ->
        br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = notice.message,
            positiveLabel = notice.positiveLabel,
            onPositive = { actions.acceptNotificationNotice(notice) },
            negativeLabel = notice.negativeLabel,
            onNegative = { actions.dismissNotificationNotice(notice) },
            onDismiss = { actions.dismissNotificationNotice(notice) }
        )
    }
    homeState.betEntryError?.takeUnless {
        showLocalCartReview || showPaymentNotice || showNotification ||
            homeState.notificationReadError != null ||
            homeState.notificationNotice != null
    }?.let {
        br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            actions.dismissBetEntryError
        )
    }
    if (homeState.preparingBet) {
        br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog()
    }
}
