package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart

data class RapidListUiState(
    val loading: Boolean = true,
    val serviceFailure: ServiceFailurePresentation = ServiceFailurePresentation(),
    val cart: ShoppingCart? = null,
    val readyForPayment: Boolean = false,
    val errorMessage: String? = null,
    val validationFailure: ServiceFailurePresentation = ServiceFailurePresentation(),
)
