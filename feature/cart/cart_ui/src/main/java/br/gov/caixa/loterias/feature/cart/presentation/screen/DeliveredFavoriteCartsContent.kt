package br.gov.caixa.loterias.feature.cart.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSContentState
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.presentation.components.FavoriteCartRow
import br.gov.caixa.loterias.feature.cart.presentation.components.FavoriteRouteHeading
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteTextStyle
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState
import br.gov.caixa.loterias.feature.cart.ui.R as DeliveryR

@Composable
fun FavoriteCartsContentDelivered(
    state: FavoriteCartsUiState,
    onBack: () -> Unit,
    onOpen: (Long) -> Unit,
    privateDataEnabled: Boolean,
    actions: FavoriteCartsActions,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(DeliveryR.string.ui_carrinhos_favoritos_0efd75),

        onBack = onBack,
        containerColor = LoteriasCaixaDSColorTokens.White,
        modifier = Modifier.uiId("cart.0140")
    ) { padding ->
        LoteriasCaixaDSContentState(state.loading, state.errorMessage, padding, actions.load) {
            if (!privateDataEnabled) {
                Column(Modifier.fillMaxSize().padding(padding)) {
                    FavoriteRouteHeading(
                        uiStrings.getString(DeliveryR.string.ui_aqui_estao_todos_os_seus_carrinhos_f_b20b4b),
                        uiStrings.getString(DeliveryR.string.ui_voce_pode_excluir_detalhar_e_transfo_c7e1fd)
                    )
                }
            } else if (state.carts.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(padding)) {
                    Column(Modifier.fillMaxWidth()) {
                        FavoriteRouteHeading(
                            uiStrings.getString(DeliveryR.string.ui_aqui_estao_todos_os_seus_carrinhos_f_b20b4b),
                            uiStrings.getString(DeliveryR.string.ui_voce_pode_excluir_detalhar_e_transfo_c7e1fd)
                        )
                    }
                    Text(
                        uiStrings.getString(DeliveryR.string.ui_voce_ainda_nao_possui_carrinhos_favo_baaa0f),
                        modifier = (Modifier.fillMaxWidth().padding(horizontal = 20.dp).align(Alignment.Center)).uiId("cart.0141"),
                        color = LoteriasCaixaDSColorTokens.FavoriteCartText,
                        style = favoriteTextStyle.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    (Modifier.fillMaxSize().padding(padding)).uiId("cart.0142"),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    item {
                        FavoriteRouteHeading(
                            uiStrings.getString(DeliveryR.string.ui_aqui_estao_todos_os_seus_carrinhos_f_b20b4b),
                            uiStrings.getString(DeliveryR.string.ui_voce_pode_excluir_detalhar_e_transfo_c7e1fd)
                        )
                    }
                    items(state.carts, key = { it.id }) { favorite ->
                        FavoriteCartRow(
                            favorite = favorite,
                            bets = if (state.expandedCartId == favorite.id) state.expandedBets else emptyList(),
                            expanded = state.expandedCartId == favorite.id,
                            loading = state.loadingCartId == favorite.id,
                            onToggle = { actions.toggleCart(favorite.id) },
                            onTransform = { actions.requestImport(favorite.id) },
                            onDetail = { onOpen(favorite.id) },
                            onDelete = { actions.requestCartRemoval(favorite.id) }
                        )
                    }
                }
            }
        }
    }
}
