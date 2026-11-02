package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.CartPaymentAccountDecision
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState

internal data class CurrentCartDialogActions(
    val reviewRecentPurchase: () -> Unit,
    val continueRecentPurchase: () -> Unit,
    val consumePaymentAccountDecision: () -> Unit,
    val confirmRemoval: () -> Unit,
    val dismissConfirmation: () -> Unit,
    val confirmClear: () -> Unit,
    val updateFavoriteName: (String) -> Unit,
    val submitFavoriteName: () -> Unit,
    val confirmFavoriteOverwrite: () -> Unit,
    val login: () -> Unit,
)

@Composable
internal fun CurrentCartConfirmationDialogs(state: CartUiState, actions: CurrentCartDialogActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.recentPurchaseConfirmation) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_existe_compra_efetivada_em_processam_8900c1),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = actions.reviewRecentPurchase,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = actions.continueRecentPurchase,
            onDismiss = actions.reviewRecentPurchase
        )
    }
    when (state.paymentAccountDecision) {
        CartPaymentAccountDecision.LoginRequired -> LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_para_continuar_utilizando_a_funciona_a5583f),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = {
                actions.login()
            },
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = actions.consumePaymentAccountDecision,
            onDismiss = actions.consumePaymentAccountDecision
        )

        CartPaymentAccountDecision.Suspended -> LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_ha_uma_suspensao_temporaria_ativa_pa_a64d6f),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = actions.consumePaymentAccountDecision,
            onDismiss = actions.consumePaymentAccountDecision
        )

        else -> Unit
    }
    state.pendingRemoval?.let {
        ConfirmCartDialog(
            message = uiStrings.getString(LocalizedR.string.ui_deseja_excluir_esta_aposta_do_carrin_dfea3f),
            onConfirm = actions.confirmRemoval,
            onDismiss = actions.dismissConfirmation
        )
    }
    if (state.clearConfirmation) {
        ConfirmCartDialog(
            message = uiStrings.getString(LocalizedR.string.ui_deseja_excluir_todas_as_apostas_do_c_57359b),
            onConfirm = actions.confirmClear,
            onDismiss = actions.dismissConfirmation
        )
    }
    if (state.favoriteNameDialog) {
        FavoriteNameDialog(state, actions.updateFavoriteName, actions.submitFavoriteName, actions.dismissConfirmation)
    }
    if (state.favoriteOverwriteConfirmation) {
        ConfirmCartDialog(
            message = uiStrings.getString(LocalizedR.string.ui_ja_existe_um_carrinho_favorito_com_e_c68666),
            onConfirm = actions.confirmFavoriteOverwrite,
            onDismiss = actions.dismissConfirmation
        )
    }
    if (state.favoriteSaved) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_sucesso_29bf2d),
            message = uiStrings.getString(LocalizedR.string.ui_carrinho_salvo_nos_favoritos_349648),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = actions.dismissConfirmation
        )
    }
    // Esta janela deve ficar acima dos diálogos de confirmação e nome, como na sobreposição bloqueante de requisição da CX.
    if (state.loading || state.loadingParameters || state.expirationRemovalInFlight) {
        br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSAuthenticationLoadingDialog()
    }
}
