package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.cart.domain.model.LocalCartReviewSnapshot
import br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository
import br.gov.caixa.loterias.feature.cart.domain.repository.VisitorCartRepository

/** Conciliação somente para leitura: um envio incerto nunca é repetido aqui. */
class GetLocalCartReviewUseCase(
    private val sessions: AuthenticatedSessionIdentityProvider,
    private val transfer: CartTransferRepository,
    private val local: VisitorCartRepository,
    private val remote: GetCurrentCartUseCase,
) {
    suspend operator fun invoke(identity: AuthenticatedSessionIdentity?): LocalCartReviewSnapshot {
        check(identity != null && sessions.isCurrent(identity))
        val review = transfer.review(identity.ownerKey)
        val pending = local.current().let { cart ->
            val items = cart.items.filter { item -> review?.bets?.any { bet -> bet.localId == item.id } == true }
            cart.copy(items = items, total = items.sumOf { it.amount }, totalBetCount = items.size)
        }
        val accountCart = when (val result = remote()) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> error("Consulta indisponível")
        }
        check(sessions.isCurrent(identity))
        return LocalCartReviewSnapshot(pending, accountCart, review)
    }
}
