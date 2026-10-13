package br.gov.caixa.loterias.core.domain.account

fun interface LocalSessionDataRepository {
    /** Clears only persisted identity/login credentials; throws if completion cannot be confirmed. */
    suspend fun clear()
}

fun interface ClearLocalSessionDataUseCase { suspend operator fun invoke() }

class ClearLocalSessionData(private val repository: LocalSessionDataRepository) : ClearLocalSessionDataUseCase {
    override suspend fun invoke() = repository.clear()
}
