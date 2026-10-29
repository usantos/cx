package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.cart.CaixaDSCartSummary
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState

@Composable
internal fun BoxScope.HomeCartSummary(homeState: HomeUiState, hasCart: Boolean, actions: HomeActions) {
    if (hasCart) {
        CaixaDSCartSummary(
            total = homeState.cartTotal,
            betCount = homeState.cartItemCount,
            modifier = (Modifier.align(Alignment.BottomCenter)).uiId("home.0021"),
            onClick = actions.openCart
        )
    }
}
