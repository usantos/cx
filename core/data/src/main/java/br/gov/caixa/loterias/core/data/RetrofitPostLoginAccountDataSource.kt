package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.cart.LegacyLocalCartBet
import br.gov.caixa.loterias.core.network.AccountVerificationBackendProvider
import br.gov.caixa.loterias.core.network.AccountVerificationEnvelopeDto
import br.gov.caixa.loterias.core.network.AccountVerificationRequestDto
import br.gov.caixa.loterias.core.network.AccountVerificationSession

/** The composition root binds both callbacks to the SAME captured identity. */
fun interface AccountVerificationSessionBinding {
    fun bind(identity: AuthenticatedSessionIdentity): AccountVerificationSession
}

/** Separate from ordinary account refreshes: those must never implicitly upload local bets. */
class RetrofitPostLoginAccountDataSource(
    private val provider: AccountVerificationBackendProvider,
    private val sessions: AccountVerificationSessionBinding,
) {
    suspend fun verify(
        identity: AuthenticatedSessionIdentity,
        bets: List<LegacyLocalCartBet>,
    ): AccountVerificationEnvelopeDto = provider.current().verify(
        AccountVerificationRequestDto(bets.map { it.toUploadDto() }), sessions.bind(identity),
    )
}
