package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.domain.repository.BettingRepository

class AddComboToCart(private val repository: BettingRepository) : AddComboToCartUseCase {
    override suspend fun invoke(comboId: Int, quantity: Int, surprise: Boolean) = appResultOf {
        require(comboId > 0) { messageText("combo_invalido_db3401") }
        require(quantity > 0) { messageText("quantidade_invalida_2eede1") }
        repository.addCombo(comboId, quantity, surprise)
    }
}
