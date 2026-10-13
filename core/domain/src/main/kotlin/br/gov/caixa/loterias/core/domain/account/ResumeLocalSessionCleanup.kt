package br.gov.caixa.loterias.core.domain.account

fun interface LocalSessionCleanupRecoveryRepository {
    /** True only when a persisted pending cleanup was completed; read/write failure must throw. */
    suspend fun resumePending(): Boolean
}

enum class LocalSessionRecovery { NONE_PENDING, RECOVERED }

fun interface ResumeLocalSessionCleanupUseCase { suspend operator fun invoke(): LocalSessionRecovery }

class ResumeLocalSessionCleanup(private val repository: LocalSessionCleanupRecoveryRepository) : ResumeLocalSessionCleanupUseCase {
    override suspend fun invoke(): LocalSessionRecovery =
        if (repository.resumePending()) LocalSessionRecovery.RECOVERED else LocalSessionRecovery.NONE_PENDING
}
