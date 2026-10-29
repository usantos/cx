package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.account.domain.model.AccountRegistration
import br.gov.caixa.loterias.feature.account.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepository

class RegisterAccount(private val repository: AccountRepository) : RegisterAccountUseCase {
    override suspend fun invoke(registration: AccountRegistration) = appResultOf {
        require(registration.acceptsTerms) { messageText("e_necessario_aceitar_os_termos_d_ae9866") }
        require(registration.zipCode.filter(Char::isDigit).length == 8) { messageText("cep_invalido_8b6688") }
        require(registration.municipalityId.stateId > 0 && registration.municipalityId.number > 0) {
            messageText("municipio_invalido_d31036")
        }
        repository.register(registration)
    }
}
