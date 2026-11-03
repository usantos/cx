package br.gov.caixa.loterias.feature.tickets.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSAlertDialog as AlertDialog
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSBasicTextField as BasicTextField
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSTextButton as TextButton
import br.gov.caixa.loterias.core.designsystem.layout.loteriasCaixaDSModalWidth
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.tickets.R as LocalizedR
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.state.TicketsUiState
import br.gov.caixa.loterias.feature.tickets.ui.R as DeliveryR

@Composable
fun FavoritePurchaseDialog(state: TicketsUiState, actions: FavoritePurchaseActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    AlertDialog(
        modifier = (Modifier.loteriasCaixaDSModalWidth()).uiId("tickets.0035"),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = actions.closeFavoriteDialog,
        title = { Text(uiStrings.getString(DeliveryR.string.ui_carrinho_favorito_fc14c7), modifier = Modifier.uiId("tickets.0036")) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BasicTextField(
                    value = state.favoriteName,
                    onValueChange = actions.changeFavoriteName,
                    modifier = (
                        Modifier.fillMaxWidth().height(52.dp)
                            .border(1.dp, LoteriasCaixaDS.colors.brand, RoundedCornerShape(4.dp)).padding(14.dp)
                        ).uiId("tickets.0037"),
                    singleLine = true,
                    decorationBox = { field ->
                        Box {
                            if (state.favoriteName.isBlank()) {
                                Text(
                                    uiStrings.getString(DeliveryR.string.ui_nome_do_carrinho_3c05fa),
                                    color = LoteriasCaixaDS.colors.mutedText,
                                    modifier = Modifier.uiId("tickets.0038")
                                )
                            }
                            field()
                        }
                    }
                )
                if (state.bets.any(ConfirmedBet::surprise)) {
                    FilterCheck(
                        uiStrings.getString(DeliveryR.string.ui_manter_surpresinhas_8d84c2),
                        state.keepSurprises,
                        actions.toggleKeepSurprises
                    )
                }
                state.errorMessage?.let { Text(it, color = LoteriasCaixaDS.colors.error, modifier = Modifier.uiId("tickets.0039")) }
            }
        },
        confirmButton = {
            TextButton(onClick = actions.validateAndSaveFavorite, modifier = Modifier.uiId("tickets.0040")) {
                Text(
                    uiStrings.getString(DeliveryR.string.ui_ok_565339),
                    modifier = Modifier.uiId("tickets.0041")
                )
            }
        },
        dismissButton = {
            TextButton(onClick = actions.closeFavoriteDialog, modifier = Modifier.uiId("tickets.0042")) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_cancelar_bb9dbb),
                    modifier = Modifier.uiId("tickets.0043")
                )
            }
        }
    )
}
