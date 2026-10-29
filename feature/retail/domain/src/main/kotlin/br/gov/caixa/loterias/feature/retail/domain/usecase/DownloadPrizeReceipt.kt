package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.retail.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.domain.repository.PrizeRedemptionRepository

class DownloadPrizeReceipt(private val repository: PrizeRedemptionRepository) : DownloadPrizeReceiptUseCase {
    override suspend fun invoke(betId: Long) = appResultOf {
        require(betId > 0) { messageText("aposta_invalida_de9ad6") }
        repository.prizeReceiptPdf(betId)
    }
}
