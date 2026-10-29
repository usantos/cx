package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.feature.account.data.dto.AccountVerificationEnvelopeDto
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartBet

fun interface PostLoginAccountDataSource {
    suspend fun verify(identity: AuthenticatedSessionIdentity, bets: List<LocalCartBet>): AccountVerificationEnvelopeDto
}
