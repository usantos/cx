package br.gov.caixa.loterias.feature.responsiblegaming.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.ResponsibleGamingRepository

class SuspendAccount(private val repository: ResponsibleGamingRepository) : SuspendAccountUseCase {
    override suspend fun invoke(periodHours: Int) = appResultOf {
        require(periodHours in setOf(24, 48, 168, 360, 720)) { messageText("periodo_de_suspensao_invalido_23733b") }
        repository.suspendAccount(periodHours)
    }
}
