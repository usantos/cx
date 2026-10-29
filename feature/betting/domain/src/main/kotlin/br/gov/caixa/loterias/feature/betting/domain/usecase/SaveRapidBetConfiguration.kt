package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.betting.domain.model.RapidBetConfiguration
import br.gov.caixa.loterias.feature.betting.domain.repository.RapidBettingRepository

class SaveRapidBetConfiguration(private val repository: RapidBettingRepository) : SaveRapidBetConfigurationUseCase {
    override suspend fun invoke(configuration: RapidBetConfiguration) = try {
        require(configuration.minimumAmount >= BigDecimal.ZERO) { messageText("valor_minimo_invalido_9a7500") }
        require(configuration.maximumAmount >= configuration.minimumAmount) { messageText("valor_maximo_deve_ser_maior_que_6a7402") }
        require(configuration.minimumPrize >= BigDecimal.ZERO) { messageText("premio_minimo_invalido_0cfb85") }
        require(configuration.modalities.isNotEmpty()) { messageText("selecione_pelo_menos_uma_modalid_e9a6ca") }
        require(configuration.requiredNumbers.intersect(configuration.excludedNumbers).isEmpty()) {
            messageText("um_numero_nao_pode_ser_obrigator_3391bd")
        }
        repository.save(configuration)
    } catch (invalid: IllegalArgumentException) {
        ServiceResult.Failed(AppError.Validation(invalid.message.orEmpty()))
    }
}
