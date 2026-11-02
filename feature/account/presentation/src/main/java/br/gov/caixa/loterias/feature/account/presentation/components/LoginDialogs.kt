package br.gov.caixa.loterias.feature.account.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState

internal data class LoginDialogActions(
    val enableBiometric: () -> Unit,
    val dismissEnrollment: () -> Unit,
    val confirmDisable: (Boolean) -> Unit,
    val acknowledgeBlocked: () -> Unit,
    val acknowledgeConsultation: () -> Unit,
)

@Composable
internal fun LoginDialogs(state: LoginUiState, actions: LoginDialogActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.biometricEnrollmentPending) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_necessario_habilitar_biometria_82b2c8),
            message = uiStrings.getString(LocalizedR.string.ui_deseja_habilitar_a_biometria_989f23),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_habilitar_2d7376),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
            onPositive = actions.enableBiometric,
            onNegative = actions.dismissEnrollment
        )
    } else if (state.biometricDisablePending) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_desativar_biometria_fead3c),
            message = uiStrings.getString(LocalizedR.string.ui_desaja_desativar_a_biometria_e_acess_5635c3),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onPositive = { actions.confirmDisable(true) },
            onNegative = { actions.confirmDisable(false) }
        )
    } else if (state.blockedAccountCheckFailed) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_concluir_esta_opera_25b771),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = actions.acknowledgeBlocked
        )
    } else if (state.consultationOnlyNoticePending) {
        br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_usuario_habilitado_apenas_para_consu_b9a66b),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = actions.acknowledgeConsultation
        )
    }
}
