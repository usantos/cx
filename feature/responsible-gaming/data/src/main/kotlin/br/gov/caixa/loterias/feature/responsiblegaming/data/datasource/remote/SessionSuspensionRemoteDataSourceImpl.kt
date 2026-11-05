package br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote

import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionExpiredException
import br.gov.caixa.loterias.core.network.dto.responsiblegaming.SuspendAccountRequestDto
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider

/** GET e POST compartilham o mesmo vínculo; nenhum deles usa o login genérico como fallback. */
internal class SessionSuspensionRemoteDataSourceImpl(
    private val provider: LoteriasApiProvider,
    private val session: AccountVerificationSession?,
) : ResponsibleGamingRemoteDataSource by ResponsibleGamingRemoteDataSourceImpl(provider) {
    override suspend fun currentUserProfile() = provider.current().currentBoundUserProfile(requireSession()).payload

    override suspend fun suspendAccount(request: SuspendAccountRequestDto) =
        provider.current().suspendBoundAccount(request, requireSession()).payload

    private fun requireSession(): AccountVerificationSession = session ?: throw AccountVerificationSessionExpiredException()
}
