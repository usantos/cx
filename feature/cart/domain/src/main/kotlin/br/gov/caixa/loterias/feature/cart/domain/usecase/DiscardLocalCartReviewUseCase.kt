package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import kotlinx.coroutines.flow.first
import br.gov.caixa.loterias.feature.cart.domain.model.CartReview
import br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository

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
