package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIconButton as IconButton
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSCurrentCartDeleteIcon
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSPurchaseFavoriteIcon
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.mapper.canClearCart
import br.gov.caixa.loterias.feature.cart.presentation.mapper.canFavoriteCart
import br.gov.caixa.loterias.feature.cart.presentation.screen.CartError
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
    CaixaDSScaffold(
        title = uiStrings.getString(LocalizedR.string.ui_carrinho_de_apostas_23d953),

        gradientTopBar = false,
        topBarEndColor = CurrentCartBlue,

        onBack = onBack,
        containerColor = CaixaDSColorTokens.White,
        topBarActionWidth = ((if (canClearCart(cart)) 44 else 0) + (if (canFavoriteCart(cart, state.authenticated)) 44 else 0) + 16).dp,
        topBarAction = {
            Row(
                Modifier.fillMaxSize().padding(end = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canClearCart(cart)) {
                    IconButton(onClick = actions.requestClear, modifier = Modifier.size(44.dp)) {
                        Icon(
                            CaixaDSCurrentCartDeleteIcon,
                            uiStrings.getString(LocalizedR.string.ui_excluir_todas_as_apostas_bc0d0d),
                            Modifier.size(20.dp),
                            tint = CaixaDSColorTokens.White
                        )
                    }
                }
                if (canFavoriteCart(cart, state.authenticated)) {
                    IconButton(onClick = actions.requestFavorite, modifier = Modifier.size(44.dp)) {
                        Icon(
                            CaixaDSPurchaseFavoriteIcon,
                            uiStrings.getString(LocalizedR.string.ui_salvar_carrinho_favorito_97b999),
                            Modifier.size(20.dp),
                            tint = CaixaDSColorTokens.White
                        )
                    }
                }
            }
        },
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
        when {
            state.loading && cart == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CaixaDS.colors.teal)
            }

            state.errorMessage != null && cart == null -> CartError(
                requireNotNull(state.errorMessage),
                padding,
                actions.load
            )

            else -> content(padding)
        }
    }
}

internal data class CurrentCartActions(
    val requestClear: () -> Unit,
    val requestFavorite: () -> Unit,
    val requestPayment: () -> Unit,
    val load: () -> Unit,
)
