package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSLoading
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel

/** Diálogos compartilhados da ação de repetição na lista e nos detalhes; preserva as decisões e callbacks do ViewModel. */
@Composable
fun BetRepeatActionsOverlay(
    viewModel: BetDetailViewModel,
    showLoading: Boolean = true,
    onServiceRedirect: ((br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect) -> Unit)? = null,
    onCartChanged: (() -> Unit)? = null,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (onServiceRedirect != null) {
        val failure = state.serviceFailure
        LaunchedEffect(failure) {
            if (failure is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RedirectRequired) {
                viewModel.dismissServiceFailure()
                onServiceRedirect(failure.destination)
            }
        }
        if (failure != null && failure !is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RedirectRequired) {
            CaixaDSAlertDialog(
                uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
                when (failure) {
                    is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.NoticeRequired ->
                        failure.notice.message ?: uiStrings.getString(
                            LocalizedR.string.ui_nao_foi_possivel_consultar_esta_apos_e725f2
                        )

                    is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RegistrationRequired -> failure.message

                    else -> uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_acessar_o_servico_t_9c588c)
                },
                stringResource(LocalizedR.string.ui_entendi_9f9373),
                viewModel::dismissServiceFailure
            )
        }
    }
    if (showLoading && state.loading) {
        Box(Modifier.fillMaxSize().background(CaixaDS.colors.surface.copy(alpha = 0.88f)), contentAlignment = Alignment.Center) {
            CaixaDSLoading("Carregando…", modifier = Modifier.uiId("tickets.0001"))
        }
    }
    BetRepeatDialogs(state, viewModel, onCartChanged)
}
