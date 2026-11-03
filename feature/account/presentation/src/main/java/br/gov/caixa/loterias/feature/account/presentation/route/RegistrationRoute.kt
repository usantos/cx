package br.gov.caixa.loterias.feature.account.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.screen.RegistrationActions
import br.gov.caixa.loterias.feature.account.presentation.screen.RegistrationScreen
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.RegistrationViewModel
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.canRegister

@Composable
fun RegistrationRoute(
    viewModel: RegistrationViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onTerms: () -> Unit,
    completionRequired: Boolean = false,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    var showZipCodeRedirect by remember { mutableStateOf(false) }
    LaunchedEffect(state.registered) {
        if (viewModel.consumeCompletion()) onComplete()
    }
    RegistrationScreen(
        state,
        canRegister(state),
        RegistrationActions(
            viewModel::changeGender,
            viewModel::changeZipCode,
            viewModel::toggleTerms,
            viewModel::toggleNotifications,
            viewModel::submit
        ),
        onBack,
        onTerms,
        onZipCodeRedirect = { showZipCodeRedirect = true },
        access = if (completionRequired) FeatureEntryAccess.REQUIRED else FeatureEntryAccess.OPTIONAL
    )
    if (state.loading) LoteriasCaixaDSAuthenticationLoadingDialog()
    if (showZipCodeRedirect) {
        ZipCodeRedirectDialog(
            onConfirm = {
                showZipCodeRedirect = false
                uriHandler.openUri("https://buscacepinter.correios.com.br/app/endereco/index.php")
            },
            onDismiss = { showZipCodeRedirect = false }
        )
    }
}

@Composable
private fun ZipCodeRedirectDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSDialog(
        title = uiStrings.getString(LocalizedR.string.ui_nao_sei_o_meu_cep_eb7e08),
        message = uiStrings.getString(LocalizedR.string.ui_voce_sera_redirecionado_para_o_site_61dd1a),
        positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
        onPositive = onConfirm,
        negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
        onNegative = onDismiss
    )
}
