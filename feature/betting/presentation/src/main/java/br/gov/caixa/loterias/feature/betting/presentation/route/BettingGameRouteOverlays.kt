package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoadingDialog
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingMessageDialog
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

@Composable
internal fun BettingGameNavigationOverlays(
    state: BettingGameUiState,
    showExitConfirmation: Boolean,
    showHelp: Boolean,
    onConfirmExit: () -> Unit,
    onDismissExit: () -> Unit,
    onDismissHelp: () -> Unit,
) {
    // SimularApostaModel -> AdicionarApostaCarrinhoHelper -> AlertDialogUtils
    // mantém o bilhete renderizado atrás de LoadingViewLoterias durante a operação.
    if (state.loading) LoteriasCaixaDSAuthenticationLoadingDialog()
    if (showExitConfirmation) BettingExitConfirmation(onConfirmExit, onDismissExit)
    if (showHelp && state.game != null) BettingHelpDialog(state, onDismissHelp)
}

@Composable
internal fun BettingGameNumberQuantityOverlay(
    state: BettingGameUiState,
    visible: Boolean,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    if (visible) BettingNumberQuantityDialog(state, onSelect, onDismiss)
}

@Composable
internal fun BettingGameComplementOverlays(
    state: BettingGameUiState,
    viewModel: BettingGameViewModel,
    showCloverQuantity: Boolean,
    showRecurring: Boolean,
    onDismissClover: () -> Unit,
    onDismissRecurring: () -> Unit,
) {
    if (showCloverQuantity) BettingCloverQuantityDialog(state, viewModel::chooseCloverQuantity, onDismissClover)
    if (showRecurring) BettingRecurringDialog(state, viewModel::chooseRecurringDraws, onDismissRecurring)
}

@Composable
internal fun BettingEmptyCartOverlay(visible: Boolean, onDismiss: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (visible) {
        BettingMessageDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_seu_carrinho_esta_vazio_6e7dbb),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = onDismiss
        )
    }
}
