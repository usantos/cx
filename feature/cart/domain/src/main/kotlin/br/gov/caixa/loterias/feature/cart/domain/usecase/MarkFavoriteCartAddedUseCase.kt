package br.gov.caixa.loterias.feature.cart.domain.usecase

fun interface MarkFavoriteCartAddedUseCase {
    suspend operator fun invoke(cartId: Long)
}
