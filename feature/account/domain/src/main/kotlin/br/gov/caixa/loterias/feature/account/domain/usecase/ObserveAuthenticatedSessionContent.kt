package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository

class ObserveAuthenticatedSessionContent(private val repository: AuthenticatedSessionContentRepository) :
    ObserveAuthenticatedSessionContentUseCase {
    override fun invoke() = repository.observe()
}
