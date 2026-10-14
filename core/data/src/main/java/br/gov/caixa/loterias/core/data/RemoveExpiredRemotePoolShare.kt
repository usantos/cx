package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.common.appResultOf
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import br.gov.caixa.loterias.core.domain.cart.RemoveExpiredPoolShareUseCase
import br.gov.caixa.loterias.core.network.AccountVerificationSession
import br.gov.caixa.loterias.core.network.CartDto

fun interface BoundCartRemovalRemoteDataSource {
    suspend fun removeBoundCartBet(id: Long, session: AccountVerificationSession): CartDto?
}

class RemoveExpiredRemotePoolShare(
    private val remote: BoundCartRemovalRemoteDataSource,
    private val bind: (AuthenticatedSessionIdentity) -> AccountVerificationSession,
) : RemoveExpiredPoolShareUseCase {
    override suspend fun invoke(item: CartItem, identity: AuthenticatedSessionIdentity) = appResultOf {
        require(item.kind == CartItemKind.POOL_SHARE && item.poolShare != null) {
            "A remoção automática exige uma reserva de cota"
        }
        remote.removeBoundCartBet(item.id, bind(identity)).toDomainCart()
    }
}
