package br.gov.caixa.loterias.feature.cart.domain.usecase

fun interface WasFavoriteCartAddedUseCase {
    suspend operator fun invoke(cartId: Long): Boolean
}
