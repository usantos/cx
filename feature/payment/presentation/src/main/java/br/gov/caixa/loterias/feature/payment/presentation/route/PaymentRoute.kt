package br.gov.caixa.loterias.feature.payment.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.feature.payment.R as LocalizedR
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentFailureScreen
import br.gov.caixa.loterias.feature.payment.presentation.screen.PixGuidanceScreen
import br.gov.caixa.loterias.feature.payment.presentation.screen.ProcessingScreen
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentTerminalEffect
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.PaymentTerminalViewModel

/**
 * UI local apenas para a orientação Pix e estados terminais. Dados e regras de cartão/Pix ficam
 * nos fluxos [PaymentCardFlowRoute] e [PaymentDataRoute], ligados aos respectivos casos de uso.
 */
@Composable
fun PaymentTerminalRoute(
    viewModel: PaymentTerminalViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onNavigate: (String) -> Unit = {},
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                PaymentTerminalEffect.NavigateBack -> onBack()
                PaymentTerminalEffect.Completed -> onComplete()
            }
        }
    }
    when (state.spec.route) {
        "payment/confirmation", "payment/success" -> ProcessingScreen(state.spec, onNavigate)
        "payment/failure" -> PaymentFailureScreen(state.spec, onNavigate)
        "payment/pix-guidance" -> PixGuidanceScreen(state.spec, state, viewModel::back, viewModel::togglePixTerms, viewModel::submit)
        else -> error(uiStrings.getString(LocalizedR.string.ui_rota_terminal_de_pagamento_desconhec_7c872b, state.spec.route))
    }
}
