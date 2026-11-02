package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.home.R as LocalizedR

@Composable
internal fun HomeRouteOverlays(
    context: HomeRouteContext,
    scene: HomeRouteScene,
    failure: androidx.compose.runtime.MutableState<Boolean>,
    openWomenSupport: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val uriHandler = LocalUriHandler.current
    var womenSupportFailed by failure
    if (scene.classic && scene.homeState.results.pendingPage != null) {
        br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoadingDialog()
    }
    if (scene.classic) {
        scene.homeState.results.error?.let { message ->
            LoteriasCaixaDSDialog(
                title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                message = message,
                positiveLabel = uiStrings.getString(LocalizedR.string.ui_ok_565339),
                onPositive = context.viewModel.resultActions::dismissError
            )
        }
    }
    if (womenSupportFailed) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_abrir_o_whatsapp_do_dbe6b0),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onPositive = openWomenSupport,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = { womenSupportFailed = false }
        )
    }
    HomeRouteDialogs(
        homeState = scene.homeState,
        lifecycleState = scene.lifecycle,
        onOpen = context.onOpen,
        actions = HomeDialogActions(
            acknowledgeLocalCartReview = context.viewModel::acknowledgeLocalCartReview,
            acknowledgeUnidentifiedPayment = context.viewModel::acknowledgeUnidentifiedPayment,
            closeNotification = context.viewModel::closeNotification,
            dismissNotificationError = context.viewModel::dismissNotificationError,
            acceptNotificationNotice = context.viewModel::acceptNotificationNotice,
            dismissNotificationNotice = context.viewModel::dismissNotificationNotice,
            dismissBetEntryError = context.viewModel::dismissBetEntryError,
            openNotificationLink = {
                context.viewModel.notificationLink()?.let { link ->
                    try {
                        uriHandler.openUri(link)
                    } catch (_: Exception) {
                        context.viewModel.notificationLinkFailed()
                    }
                }
            }
        )
    )
    scene.preferences?.errorMessage?.let { message ->
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_ok_565339),
            onPositive = { context.preferencesViewModel?.dismissError() }
        )
    }
}
