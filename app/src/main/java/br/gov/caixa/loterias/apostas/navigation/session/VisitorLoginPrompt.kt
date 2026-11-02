package br.gov.caixa.loterias.apostas.navigation.session

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.apostas.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.core.navigation.workflow.VisitorLoginCallbacks

@Composable
internal fun VisitorLoginPrompt(state: AppSessionState, navController: androidx.navigation.NavHostController) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    state.visitorLoginRequest?.let { route ->
        val callbacks = VisitorLoginCallbacks(
            route = route,
            navController = navController,
            pendingRequest = { state.visitorLoginRequest },
            clearRequest = { state.visitorLoginRequest = null },
            continueAfterLogin = { state.visitorReturnRoute = it }
        )
        br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_para_continuar_utilizando_a_funciona_a5583f),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            visitorPrompt = true,
            onPositive = callbacks::onPositive,
            onNegative = callbacks::onNegative
        )
    }
}
