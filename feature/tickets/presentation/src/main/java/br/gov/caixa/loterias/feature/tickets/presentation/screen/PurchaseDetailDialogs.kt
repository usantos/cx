package br.gov.caixa.loterias.feature.tickets.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.dialog.LoteriasCaixaDSDialog
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.presentation.components.FavoritePurchaseActions
import br.gov.caixa.loterias.feature.tickets.presentation.components.FavoritePurchaseDialog
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState

@Composable
internal fun PurchaseFavoriteDialogs(
    state: TicketsUiState,
    actions: FavoritePurchaseActions,
    onConfirmOverwrite: () -> Unit,
    onCancelOverwrite: () -> Unit,
    onFavoriteSaved: () -> Unit,
    onDismissDam: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    if (state.showFavoriteDialog) {
        FavoritePurchaseDialog(
            state,
            actions
        )
    }
    if (state.favoriteOverwriteConfirmation) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_ja_existe_um_carrinho_favorito_com_e_c68666),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_sim_931345),
            onPositive = onConfirmOverwrite,
            negativeLabel = uiStrings.getString(LocalizedR.string.ui_nao_9bc947),
            onNegative = onCancelOverwrite
        )
    }
    if (state.favoriteSaved) {
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = uiStrings.getString(LocalizedR.string.ui_carrinho_favorito_salvo_com_sucesso_441141),
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = onFavoriteSaved
        )
    }
    state.damMessage?.let { message ->
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = message,
            positiveLabel = uiStrings.getString(LocalizedR.string.ui_entendi_9f9373),
            onPositive = onDismissDam
        )
    }
}

@Composable
internal fun PurchasePixNotice(
    state: TicketsUiState,
    onConsumeNotice: (br.gov.caixa.loterias.feature.tickets.presentation.mapper.TicketsPixNoticeUiState, Boolean) -> Boolean,
    onOpen: (String) -> Unit,
    onPixRegistration: (() -> Unit)?,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    state.pixPurchaseNotice?.let { notice ->
        val registration = notice.offerRegistration && onPixRegistration != null
        LoteriasCaixaDSDialog(
            title = uiStrings.getString(LocalizedR.string.ui_atencao_b0f8a4),
            message = notice.message,
            positiveLabel = when {
                registration -> uiStrings.getString(LocalizedR.string.ui_sim_931345)
                notice.offerMoreBets -> uiStrings.getString(LocalizedR.string.ui_apostar_mais_a6282e)
                else -> uiStrings.getString(LocalizedR.string.ui_entendi_9f9373)
            },
            onPositive = {
                if (onConsumeNotice(notice, notice.offerMoreBets || registration)) {
                    if (registration) {
                        onPixRegistration.invoke()
                    } else if (notice.offerMoreBets) {
                        onOpen("home")
                    }
                }
            },
            negativeLabel = when {
                registration -> uiStrings.getString(LocalizedR.string.ui_nao_9bc947)
                notice.offerMoreBets -> uiStrings.getString(LocalizedR.string.ui_entendi_9f9373)
                else -> null
            },
            onNegative = { onConsumeNotice(notice, false) }
        )
    }
}
