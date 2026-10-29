package br.gov.caixa.loterias.feature.cart.presentation.state

import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet

data class FavoriteCartsUiState(
    val loading: Boolean = true,
    val carts: List<FavoriteCart> = emptyList(),
    val bets: List<FavoriteCartBet> = emptyList(),
    val expandedCartId: Long? = null,
    val expandedBets: List<FavoriteCartBet> = emptyList(),
    val loadingCartId: Long? = null,
    val pendingImportCartId: Long? = null,
    val regularModalityName: String = "Concurso regular",
    val specialModalityName: String = "Concurso especial",
    val pendingCartRemoval: Long? = null,
    val pendingBetRemoval: Long? = null,
    val specialSelectionRequired: Boolean = false,
    val existingCartConfirmationRequired: Boolean = false,
    val imported: Boolean = false,
    val cartDeleted: Boolean = false,
    val errorMessage: String? = null,
)
