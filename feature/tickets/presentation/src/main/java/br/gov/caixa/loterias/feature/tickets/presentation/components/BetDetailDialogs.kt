package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSSelectionDialog
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.RepeatContestOption
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel

@Composable
internal fun BetDetailServiceFailureDialog(
    failure: br.gov.caixa.loterias.core.domain.service.model.ServiceFailure?,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (failure != null && failure !is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RedirectRequired) {
        LoteriasCaixaDSAlertDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = when (failure) {
                is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.NoticeRequired ->
                    failure.notice.message ?: uiStrings.getString(
                        LocalizedR.string.ui_nao_foi_possivel_consultar_esta_apos_e725f2
                    )

                is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.ConnectionFailed -> uiStrings.getString(
                    LocalizedR.string.ui_nao_foi_possivel_acessar_o_servico_v_856170
                )

                is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RegistrationRequired -> failure.message

                else -> uiStrings.getString(LocalizedR.string.ui_nao_foi_possivel_consultar_esta_apos_e725f2)
            },
            confirmText = uiStrings.getString(LocalizedR.string.ui_tentar_novamente_b9e106),
            onConfirm = onRetry,
            onDismiss = onDismiss
        )
    }
}

@Composable
internal fun BetRepeatDialogs(state: BetDetailUiState, viewModel: BetDetailViewModel, onCartChanged: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    state.errorMessage?.let {
        LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissError
        )
    }
    if (state.cartAdditionConfirmed) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_aposta_adicionada_ao_carrinho_com_su_d9668a),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = {
                viewModel.dismissCartAddition()
                onCartChanged?.invoke()
            },
            onDismiss = viewModel::dismissCartAddition
        )
    }
    if (state.repeatContestOptions.isNotEmpty()) {
        LoteriasCaixaDSSelectionDialog(
            title = uiStrings.getString(LocalizedR.string.ui_tipo_de_concurso_4a82cf),
            message = uiStrings.getString(LocalizedR.string.ui_escolha_o_tipo_de_concurso_f2315a),
            options = state.repeatContestOptions.map(RepeatContestOption::label),
            selectedIndex = state.selectedRepeatContestIndex,
            onSelect = viewModel::selectRepeatContest,
            onConfirm = viewModel::confirmRepeatContest,
            onDismiss = viewModel::dismissRepeatContest
        )
    }
    state.repeatConversion?.let { conversion ->
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = conversion.conversionMessage,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = viewModel::confirmRepeatConversion,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::dismissRepeatContest,
            onDismiss = viewModel::dismissRepeatContest
        )
    }
}
