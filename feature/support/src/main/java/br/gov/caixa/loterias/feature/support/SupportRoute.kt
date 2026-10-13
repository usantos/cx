package br.gov.caixa.loterias.feature.support

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SupportRoute(
    viewModel: SupportViewModel,
    onBack: () -> Unit,
    onAcceptTerms: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
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
    )
}
