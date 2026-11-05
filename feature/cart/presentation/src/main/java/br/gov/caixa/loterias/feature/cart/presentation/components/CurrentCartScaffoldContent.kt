package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.presentation.screen.CartError
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState

/** Mantém o conteúdo do carrinho disponível quando uma atualização está em andamento. */
@Composable
internal fun CurrentCartScaffoldContent(
    state: CartUiState,
    cart: ShoppingCart?,
    padding: PaddingValues,
    onRetry: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    when {
        state.loading && cart == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = LoteriasCaixaDS.colors.teal)
        }

        state.errorMessage != null && cart == null -> CartError(
            requireNotNull(state.errorMessage),
            padding,
            onRetry
        )

        else -> content(padding)
    }
}
