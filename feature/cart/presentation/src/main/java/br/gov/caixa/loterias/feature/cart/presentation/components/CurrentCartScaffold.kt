package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.mapper.canClearCart
import br.gov.caixa.loterias.feature.cart.presentation.mapper.canFavoriteCart
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartBlue
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState

@Composable
internal fun CurrentCartScaffold(
    state: CartUiState,
    cart: ShoppingCart?,
    onBack: () -> Unit,
    onContinueBetting: () -> Unit,
    actions: CurrentCartActions,
    content: @Composable (PaddingValues) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val showClear = canClearCart(cart)
    val showFavorite = canFavoriteCart(cart, state.authenticated)
    LoteriasCaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_carrinho_de_apostas_23d953),

        gradientTopBar = false,
        topBarEndColor = CurrentCartBlue,

        onBack = onBack,
        containerColor = LoteriasCaixaDSColorTokens.White,
        topBarActionWidth = ((if (showClear) 44 else 0) + (if (showFavorite) 44 else 0) + 16).dp,
        topBarAction = { CurrentCartHeaderActions(showClear, showFavorite, actions) },
        bottomBar = {
            CurrentCartFooter(
                cart,
                paymentEnabled = cart != null &&
                    !state.loading && !state.loadingParameters && !state.parametersUnavailable,
                onAdd = onContinueBetting,
                onPay = actions.requestPayment
            )
        }
    ) { padding ->
        CurrentCartScaffoldContent(state, cart, padding, actions.load, content)
    }
}

internal data class CurrentCartActions(
    val requestClear: () -> Unit,
    val requestFavorite: () -> Unit,
    val requestPayment: () -> Unit,
    val load: () -> Unit,
)
