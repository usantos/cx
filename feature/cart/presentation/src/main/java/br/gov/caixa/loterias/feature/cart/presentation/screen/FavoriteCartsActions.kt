package br.gov.caixa.loterias.feature.cart.presentation.screen

data class FavoriteCartsActions(
    val load: () -> Unit,
    val toggleCart: (Long) -> Unit,
    val requestImport: (Long) -> Unit,
    val requestCartRemoval: (Long) -> Unit,
)
