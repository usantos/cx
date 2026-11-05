package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.contract.account.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.core.domain.contract.account.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.cart.domain.model.CartReview
import br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository
import kotlinx.coroutines.flow.first

/** Exclui somente o recibo explicitamente revisado; mantém a proteção em torno da publicação do conteúdo. */
class DiscardLocalCartReviewUseCase(
    private val sessions: AuthenticatedSessionIdentityProvider,
    private val transfer: CartTransferRepository,
    private val content: AuthenticatedSessionContentRepository,
) {
    suspend operator fun invoke(identity: AuthenticatedSessionIdentity?, review: CartReview) {
        check(identity != null && sessions.isCurrent(identity))
        check(transfer.discardReviewed(identity.ownerKey, review))
        val existing = content.observe().first()
        if (sessions.isCurrent(identity)) check(content.publish(identity, existing?.copy(localCartReviewRequired = false)))
    }
}
