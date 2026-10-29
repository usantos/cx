package br.gov.caixa.loterias.feature.account.data.datasource.remote

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartBet
import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider
import br.gov.caixa.loterias.feature.account.data.dto.AccountVerificationEnvelopeDto
import br.gov.caixa.loterias.feature.account.data.dto.AccountVerificationRequestDto
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.feature.cart.data.mapper.toUploadDto

/** A raiz de composição vincula ambos os callbacks à MESMA identidade capturada. */
fun interface AccountVerificationSessionBinding {
    fun bind(identity: AuthenticatedSessionIdentity): AccountVerificationSession
}

/** Separado das atualizações comuns de conta: elas nunca devem enviar implicitamente apostas locais. */
class PostLoginAccountDataSourceImpl(
    private val provider: AccountVerificationBackendProvider,
    private val sessions: AccountVerificationSessionBinding,
) : PostLoginAccountDataSource {
    override suspend fun verify(
        identity: AuthenticatedSessionIdentity,
        bets: List<LocalCartBet>,
    ): AccountVerificationEnvelopeDto = provider.current().verify(
        AccountVerificationRequestDto(bets.map { it.toUploadDto() }), sessions.bind(identity),
    )
}
