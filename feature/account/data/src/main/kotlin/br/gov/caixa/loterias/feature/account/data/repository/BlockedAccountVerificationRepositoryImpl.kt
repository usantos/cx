package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.feature.account.data.datasource.remote.featureAccountApi
import br.gov.caixa.loterias.feature.account.domain.repository.BlockedAccountVerificationRepository
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider

class BlockedAccountVerificationRepositoryImpl(
    private val provider: BackendApiProvider,
) : BlockedAccountVerificationRepository {
    override suspend fun verifyBlockedAccount() {
        val response = provider.current().featureAccountApi().blockedUserStatus()
        // Este endpoint pode não ter payload. Metadados de erro ou redirecionamento não significam verificação bem-sucedida.
        check(response.code == null && response.redirect == null) {
            "Verificação do apostador não concluída"
        }
    }
}
