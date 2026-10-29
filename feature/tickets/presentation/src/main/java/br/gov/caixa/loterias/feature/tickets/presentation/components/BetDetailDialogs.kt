package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSDialog
import br.gov.caixa.loterias.core.designsystem.components.dialog.CaixaDSSelectionDialog
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.mapper.TeimosinhaNotice
import br.gov.caixa.loterias.feature.tickets.presentation.state.BetDetailUiState
import br.gov.caixa.loterias.feature.tickets.presentation.state.RepeatContestOption
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.BetDetailViewModel

@Composable
internal fun BetDetailServiceFailureDialog(
    failure: br.gov.caixa.loterias.core.domain.service.model.ServiceFailure?,
    viewModel: BetDetailViewModel,
    onBack: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (failure != null && failure !is br.gov.caixa.loterias.core.domain.service.model.ServiceFailure.RedirectRequired) {
        CaixaDSAlertDialog(
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
            onConfirm = viewModel::loadDetails,
            onDismiss = {
                viewModel.dismissServiceFailure()
                onBack()
            }
        )
    }
}

@Composable
internal fun BetDetailTeimosinhaDialog(notice: TeimosinhaNotice?, onConfirm: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSAlertDialog(
        title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
        message = buildAnnotatedString {
            if (notice == TeimosinhaNotice.ORIGINAL_PAID) {
                append(uiStrings.getString(LocalizedR.string.ui_esta_aposta_com_teimosinha_nao_e_mai_8ffde1))
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(
                        uiStrings.getString(LocalizedR.string.ui_aposta_de_troca_f0835f)
                    )
                }
                append(uiStrings.getString(LocalizedR.string.ui_gerada_apos_o_resgate_do_premio_para_ceb8bd))
            } else {
                append(uiStrings.getString(LocalizedR.string.ui_a_conferencia_dos_jogos_da_teimosinh_86f58e))
            }
        },
        confirmText = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
        onConfirm = onConfirm,
        onDismiss = { }
    )
}

@Composable
internal fun BetRepeatDialogs(state: BetDetailUiState, viewModel: BetDetailViewModel, onCartChanged: (() -> Unit)?) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    state.errorMessage?.let {
        CaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissError
        )
    }
    if (state.cartAdditionConfirmed) {
        CaixaDSDialog(
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
        CaixaDSSelectionDialog(
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
        CaixaDSDialog(
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
