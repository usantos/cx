package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepositoryFactory

/** Os comandos do formulário compartilham o mesmo vínculo adquirido antes de agendar qualquer escrita. */
class AccountFormUseCasesFactory(private val repositories: AccountRepositoryFactory) {
    fun forSession(identity: AuthenticatedSessionIdentity?): AccountFormUseCases {
        val repository = repositories.forSession(identity)
        return AccountFormUseCases(
            UpdatePersonalData(repository),
            RegisterAccount(repository),
            RecalculateAuthorizedLimit(repository)
        )
    }
}

class AccountFormUseCases(
    val updatePersonalData: UpdatePersonalDataUseCase,
    val registerAccount: RegisterAccountUseCase,
    val recalculateAuthorizedLimit: RecalculateAuthorizedLimitUseCase,
)
