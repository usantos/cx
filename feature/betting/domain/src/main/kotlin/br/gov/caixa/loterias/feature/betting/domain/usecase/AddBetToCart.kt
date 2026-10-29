package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.betting.domain.model.BetDraft
import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.domain.repository.BettingRepository

class AddBetToCart(private val repository: BettingRepository) : AddBetToCartUseCase {
    override suspend fun invoke(draft: BetDraft) = appResultOf {
        require(draft.modalityId > 0) { messageText("modalidade_invalida_4a3e50") }
        require(
            draft.numbers.isNotEmpty() || draft.surprise ||
                draft.lotecaSelections.isNotEmpty() || draft.lotogolSelections.isNotEmpty() ||
                draft.superSevenColumns.isNotEmpty()
        ) { messageText("selecione_os_numeros_ou_palpites_e95abf") }
        require(draft.quantity > 0) { messageText("quantidade_de_apostas_invalida_4cbf5a") }
        require(
            draft.numberQuantity > 0 || draft.lotecaSelections.isNotEmpty() || draft.lotogolSelections.isNotEmpty()
        ) { messageText("quantidade_de_numeros_ou_palpite_bea725") }
        repository.addBet(draft)
    }
}
