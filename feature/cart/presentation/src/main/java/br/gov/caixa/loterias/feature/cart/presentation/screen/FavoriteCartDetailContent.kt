package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSContentState
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSHorizontalDivider as HorizontalDivider
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.layout.caixaDSDrawablePainter
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.presentation.components.FavoriteBetCard
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteTextStyle
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState

@Composable
internal fun FavoriteCartDetailContent(
    state: FavoriteCartsUiState,
    onBack: () -> Unit,
    onHelp: (() -> Unit)?,
    onReload: () -> Unit,
    onRequestBetRemoval: (Long) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_favoritas_1f5b98),
        onBack = onBack,

        topBarActionWidth = 50.dp,
        topBarAction = onHelp?.let { action ->
            {
                IconButton(onClick = action, modifier = Modifier.uiId("cart.0134")) {
                    androidx.compose.foundation.Image(
                        caixaDSDrawablePainter(R.drawable.legacy_favorite_duvidas_toolbar_icon),
                        uiStrings.getString(LocalizedR.string.ui_ajuda_fe0add),
                        Modifier.size(32.dp)
                    )
                }
            }
        },
        containerColor = CaixaDSColorTokens.White,
        modifier = Modifier.uiId("cart.0135")
    ) { padding ->
        CaixaDSContentState(state.loading, state.errorMessage, padding, onReload) {
            LazyColumn(
                (Modifier.fillMaxSize().padding(padding)).uiId("cart.0136"),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                item {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_apostas_do_carrinho_favorito_escolhi_e1e1bf),
                        (Modifier.padding(20.dp))
                            .uiId("cart.0137"),
                        color = CaixaDSColorTokens.FavoriteCartText,
                        style = favoriteTextStyle.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_nesta_area_estao_todas_as_suas_apost_4883b2),
                        (Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)).uiId("cart.0138"),
                        color = CaixaDSColorTokens.SecondaryText,
                        style = favoriteTextStyle
                    )
                    HorizontalDivider(color = CaixaDSColorTokens.FavoriteDivider, modifier = Modifier.uiId("cart.0139"))
                }
                items(state.bets, key = { it.id }) { bet -> FavoriteBetCard(bet) { onRequestBetRemoval(bet.id) } }
            }
        }
    }
}
