package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.domain.repository.PrizeRedemptionRepository

class RedeemPrize(private val repository: PrizeRedemptionRepository) : RedeemPrizeUseCase {
    override suspend fun invoke(betId: Long, channel: PrizeRedemptionChannel, value: String) = appResultOf {
        require(betId > 0) { messageText("aposta_invalida_de9ad6") }
        when (channel) {
            PrizeRedemptionChannel.PIX -> {
                require(value.trim().isNotEmpty()) { messageText("chave_pix_obrigatoria_a97b4b") }
                repository.redeemPix(betId, value.trim())
            }

            PrizeRedemptionChannel.MERCADO_PAGO -> {
                require(value.trim().isNotEmpty()) { messageText("e_mail_obrigatorio_a49d54") }
                repository.redeemMercadoPago(betId, value.trim())
            }

            else -> error("O canal selecionado não realiza resgate online")
        }
    }
}
