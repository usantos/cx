package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.network.dto.account.AccountVerificationEnvelopeDto
import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartBet

fun interface PostLoginAccountDataSource {
    suspend fun verify(identity: AuthenticatedSessionIdentity, bets: List<LocalCartBet>): AccountVerificationEnvelopeDto
}
