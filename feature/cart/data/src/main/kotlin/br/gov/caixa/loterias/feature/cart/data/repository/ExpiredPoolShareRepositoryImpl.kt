package br.gov.caixa.loterias.feature.cart.data.repository

import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.BoundCartRemovalRemoteDataSource
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.repository.ExpiredPoolShareRepository

class ExpiredPoolShareRepositoryImpl(
    private val remote: BoundCartRemovalRemoteDataSource,
    private val bind: (AuthenticatedSessionIdentity) -> AccountVerificationSession,
) : ExpiredPoolShareRepository {
    override suspend fun remove(item: CartItem, identity: AuthenticatedSessionIdentity) =
        remote.removeBoundCartBet(item.id, bind(identity)).toDomainCart()
}
