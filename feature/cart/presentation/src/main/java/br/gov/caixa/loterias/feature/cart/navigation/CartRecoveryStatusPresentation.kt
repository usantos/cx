package br.gov.caixa.loterias.feature.cart.navigation

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.common.network.NetworkAvailability

/** Recursos Android do hospedeiro usados pelo espaço existente de estado de recuperação do carrinho. */
interface CartRecoveryStatusPresentation {
    @Composable
    fun Content(loading: Boolean, onRetry: () -> Unit, networkAvailability: NetworkAvailability)
}
