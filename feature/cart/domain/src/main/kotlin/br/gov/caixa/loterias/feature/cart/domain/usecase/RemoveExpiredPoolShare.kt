package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.repository.ExpiredPoolShareRepository

class RemoveExpiredPoolShare(private val repository: ExpiredPoolShareRepository) : RemoveExpiredPoolShareUseCase {
    override suspend fun invoke(item: CartItem, identity: AuthenticatedSessionIdentity) = appResultOf {
        require(item.kind == CartItemKind.POOL_SHARE && item.poolShare != null) {
            messageText("a_remocao_automatica_exige_uma_r_8c9f83")
        }
        repository.remove(item, identity)
    }
}
