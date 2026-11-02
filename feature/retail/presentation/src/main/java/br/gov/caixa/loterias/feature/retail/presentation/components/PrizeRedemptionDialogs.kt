package br.gov.caixa.loterias.feature.retail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAlertDialog
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.feature.retail.R as LocalizedR
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRegistrationNotice

@Composable
internal fun PrizeRedemptionDialogs(
    state: PrizeRedemptionUiState,
    displayState: PrizeRedemptionUiState,
    onDismissError: () -> Unit,
    onNoticeAction: (ServiceNoticeUiState) -> Unit,
    onDismissNotice: (ServiceNoticeUiState) -> Unit,
    onRegistrationAction: (PrizeRegistrationNotice) -> Unit,
    onDismissRegistration: (PrizeRegistrationNotice) -> Unit,
    onDismissSuccessNotice: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (displayState.loading && displayState.stage != PrizeRedemptionStage.LOADING) {
        if (displayState.stage == PrizeRedemptionStage.MERCADO_PAGO) {
            LoteriasCaixaDSAuthenticationLoadingDialog()
        } else {
            Box(
                Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.Black.copy(alpha = LOADING_SCRIM_ALPHA)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LoteriasCaixaDS.colors.brand)
            }
        }
    }
    state.errorMessage?.let {
        LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            onDismissError
        )
    }
    state.notice?.let { notice ->
        br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = notice.message,
            positiveLabel = notice.positiveLabel,
            negativeLabel = notice.negativeLabel,
            onPositive = { onNoticeAction(notice) },
            onNegative = { onDismissNotice(notice) }
        )
    }
    state.registrationNotice?.let { notice ->
        br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = notice.message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onPositive = { onRegistrationAction(notice) },
            onNegative = { onDismissRegistration(notice) }
        )
    }
    state.successNoticeMessage?.let {
        LoteriasCaixaDSAlertDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            it,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            onDismissSuccessNotice
        )
    }
}

private const val LOADING_SCRIM_ALPHA = .28f
