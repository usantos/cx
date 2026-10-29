package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.retail.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.domain.repository.PrizeRedemptionRepository

class ConsultPixRedemptionKey(private val repository: PrizeRedemptionRepository) : ConsultPixRedemptionKeyUseCase {
    override suspend fun invoke(betId: Long, key: String) = appResultOf {
        require(betId > 0) { messageText("aposta_invalida_de9ad6") }
        require(key.trim().isNotEmpty()) { messageText("chave_pix_obrigatoria_a97b4b") }
        repository.consultPixKey(betId, key.trim())
    }
}
