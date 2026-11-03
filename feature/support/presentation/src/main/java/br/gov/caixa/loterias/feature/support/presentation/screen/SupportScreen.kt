package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportMode
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

@Composable
fun SupportScreen(
    state: SupportUiState,
    onAction: (SupportAction) -> Unit,
    faqContent: @Composable (SupportUiState, (SupportAction) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading && state.mode != SupportMode.GAME_HELP) LoteriasCaixaDSAuthenticationLoadingDialog()
    if (state.mode == SupportMode.TERMS_READ_ONLY) {
        ReadOnlyTermsScreen(state, { onAction(SupportAction.Back) }, modifier)
        return
    }
    if (state.mode == SupportMode.GAME_HELP) {
        GameHelpScreen(
            state = state,
            onBack = { onAction(SupportAction.Back) },
            modifier = modifier
        )
        return
    }
    SupportContent(state, onAction, faqContent, modifier)
}
