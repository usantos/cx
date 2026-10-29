package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.repository.LocalSessionDataRepository

class ClearLocalSessionData(private val repository: LocalSessionDataRepository) : ClearLocalSessionDataUseCase {
    override suspend fun invoke() = repository.clear()
}
