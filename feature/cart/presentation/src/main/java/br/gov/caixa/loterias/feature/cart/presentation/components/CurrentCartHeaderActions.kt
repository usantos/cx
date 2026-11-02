package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSCurrentCartDeleteIcon
import br.gov.caixa.loterias.core.designsystem.icon.LoteriasCaixaDSPurchaseFavoriteIcon
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R as LocalizedR

/** A visibilidade é calculada pelo scaffold e também determina a largura reservada no topo. */
@Composable
internal fun CurrentCartHeaderActions(showClear: Boolean, showFavorite: Boolean, actions: CurrentCartActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        Modifier.fillMaxSize().padding(end = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showClear) {
            IconButton(onClick = actions.requestClear, modifier = Modifier.size(44.dp).uiId("cart.0143")) {
                Icon(
                    LoteriasCaixaDSCurrentCartDeleteIcon,
                    uiStrings.getString(LocalizedR.string.ui_excluir_todas_as_apostas_bc0d0d),
                    Modifier.size(20.dp),
                    tint = LoteriasCaixaDSColorTokens.White
                )
            }
        }
        if (showFavorite) {
            IconButton(onClick = actions.requestFavorite, modifier = Modifier.size(44.dp).uiId("cart.0144")) {
                Icon(
                    LoteriasCaixaDSPurchaseFavoriteIcon,
                    uiStrings.getString(LocalizedR.string.ui_salvar_carrinho_favorito_97b999),
                    Modifier.size(20.dp),
                    tint = LoteriasCaixaDSColorTokens.White
                )
            }
        }
    }
}
