package br.gov.caixa.loterias.feature.account.presentation.route

import br.gov.caixa.loterias.feature.account.R as LocalizedR

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.account.presentation.screen.PersonalDataActions
import br.gov.caixa.loterias.feature.account.presentation.screen.PersonalDataScreen
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel

@Composable
fun PersonalDataRoute(viewModel: PersonalDataViewModel, onBack: () -> Unit, onComplete: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(viewModel) {
        viewModel.onResume()
        onPauseOrDispose { }
    }
    val uriHandler = LocalUriHandler.current
    // DadosPessoais instalado mantém seu rodapé de atualização oculto enquanto adjustResize expõe o formulário acima do IME.
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    var showZipCodeRedirect by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.saved) {
        if (state.saved) onComplete()
    }

    PersonalDataScreen(
        state = state,
        imeVisible = imeVisible,
        onBack = onBack,
        actions = PersonalDataActions(
            submit = viewModel::submit,
            changeGender = viewModel::changeGender,
            changeZipCode = viewModel::changeZipCode,
            changeDailyLimit = viewModel::changeDailyLimit,
            recalculateLimit = viewModel::recalculateLimit,
            requestZipCodeRedirect = { showZipCodeRedirect = true },
            toggleNotifications = {
                val activating = !state.acceptsNotifications
                viewModel.toggleNotifications()
                notificationMessage = if (activating) {
                    uiStrings.getString(LocalizedR.string.ui_ao_ativar_esta_opcao_voce_recebera_n_1403b7)
                } else {
                    uiStrings.getString(LocalizedR.string.ui_ao_desativar_esta_opcao_voce_nao_rec_251a0d)
                }
            }
        )
    )

    if (state.loading || state.saving) CaixaDSAuthenticationLoadingDialog()

    if (showZipCodeRedirect) {
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_nao_sei_o_meu_cep_eb7e08),
            message = uiStrings.getString(LocalizedR.string.ui_voce_sera_redirecionado_para_o_site_61dd1a),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_confirmar_717bed),
            onPositive = {
                showZipCodeRedirect = false
                uriHandler.openUri(CORREIOS_ZIP_CODE_URL)
            },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onNegative = { showZipCodeRedirect = false }
        )
    }
    notificationMessage?.let { message ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = { notificationMessage = null }
        )
    }
    state.errorMessage?.let { message ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = viewModel::dismissError
        )
    }
}

private const val CORREIOS_ZIP_CODE_URL = "https://buscacepinter.correios.com.br/app/endereco/index.php"
