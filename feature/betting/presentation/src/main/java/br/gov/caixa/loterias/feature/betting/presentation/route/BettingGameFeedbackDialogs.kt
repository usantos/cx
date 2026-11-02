package br.gov.caixa.loterias.feature.betting.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingFavoriteDialog
import br.gov.caixa.loterias.feature.betting.presentation.components.BettingMessageDialog
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel

@Composable
internal fun BettingCartDialogs(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.addedToCart) {
        BettingMessageDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_aposta_adicionada_ao_carrinho_com_su_d9668a),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = viewModel::acknowledgeCartAddition
        )
    }
    if (state.dailyLimitConfirmation) {
        BettingMessageDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_o_valor_maximo_de_apostas_permitida_0e6c55) +
                uiStrings.getString(LocalizedR.string.ui_deseja_prosseguir_com_a_inclusao_da_3e386b),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onPositive = viewModel::confirmDailyLimit,
            onNegative = viewModel::cancelDailyLimit
        )
    }
    if (state.duplicateConfirmation) {
        BettingMessageDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = br.gov.caixa.loterias.feature.cart.domain.model.VISITOR_DUPLICATE_CONFIRMATION,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onPositive = viewModel::confirmLocalDuplicate,
            onNegative = viewModel::cancelLocalDuplicate
        )
    }
}

@Composable
internal fun BettingFavoriteFeedbackDialogs(state: BettingGameUiState, viewModel: BettingGameViewModel) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.favoriteExpanded) {
        BettingFavoriteDialog(state.favoriteName, viewModel::changeFavoriteName, viewModel::submitFavorite, viewModel::cancelFavorite)
    }
    if (state.favoriteOverwriteConfirmation) {
        BettingMessageDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            state.favoriteOverwriteMessage.orEmpty(),
            stringResource(LocalizedR.string.betting_recovery_yes),
            viewModel::confirmFavoriteOverwrite,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = viewModel::dismissFavoriteOverwrite
        )
    }
    state.warningMessage?.let { message ->
        BettingMessageDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message,
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissMessage
        )
    }
    if (state.game != null && state.errorMessage != null) {
        BettingMessageDialog(
            uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            requireNotNull(state.errorMessage),
            stringResource(LocalizedR.string.ui_entendi_9f9373),
            viewModel::dismissMessage
        )
    }
}
