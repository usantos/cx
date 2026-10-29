package br.gov.caixa.loterias.feature.support.presentation.screen

import br.gov.caixa.loterias.core.designsystem.components.CaixaDSAuthenticationLoadingDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMode
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

@Composable
fun SupportScreen(
    state: SupportUiState,
    onAction: (SupportAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) CaixaDSAuthenticationLoadingDialog()
    if (state.mode == SupportMode.TERMS_READ_ONLY) {
        ReadOnlyTermsScreen(state, { onAction(SupportAction.Back) }, modifier)
        return
    }
    if (state.mode == SupportMode.GAME_HELP) {
        GameHelpScreen(
            state = state,
            onBack = { onAction(SupportAction.Back) },
            modifier = modifier,
        )
        return
    }
    SupportContent(state, onAction, modifier)
}
