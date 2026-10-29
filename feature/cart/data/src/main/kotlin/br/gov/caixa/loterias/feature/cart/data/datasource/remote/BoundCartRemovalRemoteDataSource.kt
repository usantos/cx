package br.gov.caixa.loterias.feature.cart.data.datasource.remote

import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto

fun interface BoundCartRemovalRemoteDataSource {
    suspend fun removeBoundCartBet(id: Long, session: AccountVerificationSession): CartDto?
}
