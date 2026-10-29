package br.gov.caixa.loterias.feature.account.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.feature.account.R as LocalizedR
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutNotice
import br.gov.caixa.loterias.feature.account.presentation.state.LogoutUiState

@Composable
internal fun LogoutStatus(state: LogoutUiState, onDismissNotice: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.signingOut || state.clearingLocalData || state.launch != null || state.waitingForBrowser) CaixaDSAuthenticationLoadingDialog()
    state.notice?.let { notice ->
        CaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = when (notice) {
                LogoutNotice.SERVER_FAILURE -> uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_encerrar_a_sessao_n_95501b)
                LogoutNotice.LOCAL_FAILURE -> uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_encerrar_a_sessao_n_64a8f8)
                LogoutNotice.LOCAL_DATA_FAILURE -> uiStrings.getString(LocalizedR.string.ui_o_acesso_foi_encerrado_mas_nao_foi_p_0514a0)
                LogoutNotice.CANCELLED -> uiStrings.getString(LocalizedR.string.ui_voce_saiu_deste_aplicativo_o_encerra_88f3c9)
                LogoutNotice.UNCONFIRMED -> uiStrings.getString(LocalizedR.string.ui_voce_saiu_deste_aplicativo_mas_nao_f_000db1)
                LogoutNotice.SIMULATED -> uiStrings.getString(LocalizedR.string.ui_sessao_local_encerrada_no_modo_de_si_0f3d60)
            },
            positiveLabel = if (
                notice == LogoutNotice.LOCAL_DATA_FAILURE || notice == LogoutNotice.SERVER_FAILURE
            ) {
                uiStrings.getString(
                    LocalizedR.string.ui_tentar_novamente_b9e106
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_entendi_9f9373)
            },
            onPositive = onDismissNotice
        )
    }
}
