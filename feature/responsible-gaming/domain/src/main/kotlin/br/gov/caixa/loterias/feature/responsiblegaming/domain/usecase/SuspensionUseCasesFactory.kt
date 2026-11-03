package br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase

import br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.SuspensionRepositoryFactory

class SuspensionUseCasesFactory(private val repositories: SuspensionRepositoryFactory) {
    fun forCurrentSession(): SuspendAccountUseCase = SuspendAccount(repositories.forCurrentSession())
}
