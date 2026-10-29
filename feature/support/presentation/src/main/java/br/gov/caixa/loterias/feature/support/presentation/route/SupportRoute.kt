package br.gov.caixa.loterias.feature.support.presentation.route

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.feature.support.presentation.screen.SupportScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.viewmodel.SupportViewModel

@Composable
fun SupportRoute(viewModel: SupportViewModel, onBack: () -> Unit, onAcceptTerms: () -> Unit) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    BackHandler(enabled = state.selectedSectionId != null) {
        viewModel.onAction(SupportAction.CloseSection)
    }
    LaunchedEffect(viewModel) { viewModel.termsAccepted.collect { onAcceptTerms() } }
    SupportScreen(
        state = state,
        onAction = { action ->
            if (action == SupportAction.Back) {
                if (state.selectedSectionId != null) {
                    viewModel.onAction(SupportAction.CloseSection)
                } else {
                    onBack()
                }
            } else {
                viewModel.onAction(action)
            }
        },
        modifier = Modifier.uiId("help.0003")
    )
}
