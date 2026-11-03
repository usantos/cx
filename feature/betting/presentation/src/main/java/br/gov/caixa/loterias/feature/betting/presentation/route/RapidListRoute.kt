package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.components.ServiceFailureOverlay
import br.gov.caixa.loterias.feature.betting.presentation.screen.RapidListScreen
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.RapidListViewModel

@Composable
fun RapidListRoute(
    viewModel: RapidListViewModel,
    onBack: () -> Unit,
    onPayment: () -> Unit,
    onValidationRedirect: (ServiceRedirect) -> Unit,
    onServiceRedirect: (ServiceRedirect) -> Unit = onValidationRedirect,
    onConfiguration: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ServiceFailureOverlay(
        state.serviceFailure,
        viewModel::acceptServiceNotice,
        viewModel::dismissServiceNotice,
        viewModel::consumeServiceRedirect,
        onServiceRedirect
    )
    ServiceFailureOverlay(
        state.validationFailure,
        viewModel::acceptValidationNotice,
        viewModel::dismissValidationNotice,
        viewModel::consumeValidationRedirect,
        onValidationRedirect
    )
    LaunchedEffect(state.readyForPayment) {
        if (state.readyForPayment) {
            viewModel.consumePaymentReady()
            onPayment()
        }
    }
    RapidListScreen(
        state = state,
        onBack = onBack,
        onConfiguration = onConfiguration,
        onGenerate = { viewModel.generate() },
        onAdvance = viewModel::advance,
        onRemove = viewModel::remove,
        onDismissError = viewModel::dismissError
    )
}
