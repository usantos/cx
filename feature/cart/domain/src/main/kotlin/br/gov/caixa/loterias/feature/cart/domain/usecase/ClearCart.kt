package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.repository.CartRepository
import br.gov.caixa.loterias.feature.cart.domain.repository.FavoriteCartUsageRepository
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

class ClearCart(
    private val repository: CartRepository,
    private val favoriteUsage: FavoriteCartUsageRepository? = null,
) : ClearCartUseCase {
    override suspend fun invoke() = appResultOf {
        val cart = repository.clear()
        coroutineContext.ensureActive()
        favoriteUsage?.clear()
        cart
    }
}
