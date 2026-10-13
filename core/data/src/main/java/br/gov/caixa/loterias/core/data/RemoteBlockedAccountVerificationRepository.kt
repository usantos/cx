package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.account.BlockedAccountVerificationRepository
import br.gov.caixa.loterias.core.network.LegacyBackendApiProvider

class RemoteBlockedAccountVerificationRepository(
    private val provider: LegacyBackendApiProvider,
) : BlockedAccountVerificationRepository {
    override suspend fun verifyBlockedAccount() {
        val response = provider.current().blockedUserStatus()
        // This endpoint may have no payload. Error/redirect metadata is not a successful check.
        check(response.code == null && response.redirect == null) {
            "Verificação do apostador não concluída"
        }
    }
}
