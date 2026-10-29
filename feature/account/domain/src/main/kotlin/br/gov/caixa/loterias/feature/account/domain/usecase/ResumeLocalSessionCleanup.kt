package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.model.LocalSessionRecovery
import br.gov.caixa.loterias.feature.account.domain.repository.LocalSessionCleanupRecoveryRepository

class ResumeLocalSessionCleanup(private val repository: LocalSessionCleanupRecoveryRepository) : ResumeLocalSessionCleanupUseCase {
    override suspend fun invoke(): LocalSessionRecovery =
        if (repository.resumePending()) LocalSessionRecovery.RECOVERED else LocalSessionRecovery.NONE_PENDING
}
