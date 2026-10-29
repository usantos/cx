package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.model.localizedMessage as messageText

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepository
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile

class UpdatePersonalData(private val repository: AccountRepository) : UpdatePersonalDataUseCase {
    override suspend fun invoke(update: PersonalDataUpdate, profile: AccountProfile) = appResultOf {
        require(update.zipCode.filter(Char::isDigit).length == 8) { messageText("cep_invalido_8b6688") }
        require(update.municipalityId.stateId > 0 && update.municipalityId.number > 0) {
            messageText("municipio_invalido_d31036")
        }
        require(update.dailyLimit >= profile.minimumDailyLimit) { messageText("limite_abaixo_do_minimo_permitid_fe1e62") }
        require(update.dailyLimit <= profile.authorizedDailyLimit) { messageText("limite_acima_do_maximo_permitido_9e41b4") }
        repository.updatePersonalData(update)
    }
}
