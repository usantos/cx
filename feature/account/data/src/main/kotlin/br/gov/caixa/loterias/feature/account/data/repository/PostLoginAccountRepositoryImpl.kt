package br.gov.caixa.loterias.feature.account.data.repository

import br.gov.caixa.loterias.feature.cart.domain.model.CartCleanupResult
import br.gov.caixa.loterias.feature.cart.domain.model.CartTransferPreparation
import br.gov.caixa.loterias.feature.cart.domain.repository.CartTransferRepository
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.repository.PostLoginAccountRepository
import br.gov.caixa.loterias.feature.account.domain.repository.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionContent
import br.gov.caixa.loterias.feature.account.domain.model.AccountNotification
import br.gov.caixa.loterias.core.network.api.AccountVerificationSessionExpiredException
import br.gov.caixa.loterias.feature.cart.data.repository.toDomainCart
import br.gov.caixa.loterias.feature.account.data.mapper.accountAccess
import br.gov.caixa.loterias.feature.account.data.datasource.remote.PostLoginAccountDataSource

class PostLoginAccountRepositoryImpl(
    private val remote: PostLoginAccountDataSource,
    private val sessionContent: AuthenticatedSessionContentRepository,
    private val transfer: CartTransferRepository? = null,
    private val isCurrent: (AuthenticatedSessionIdentity) -> Boolean = { true },
) : PostLoginAccountRepository {
    override suspend fun verifyAccess(identity: AuthenticatedSessionIdentity): AccountAccess {
        if (!isCurrent(identity)) throw AccountVerificationSessionExpiredException()
        val prepared = transfer?.prepare(identity.ownerKey) ?: CartTransferPreparation.Empty
        var reviewRequired = prepared == CartTransferPreparation.RequiresReview
        if (prepared is CartTransferPreparation.CleanupPending) {
            if (!isCurrent(identity)) throw AccountVerificationSessionExpiredException()
            reviewRequired = transfer!!.finishConfirmed(identity.ownerKey, prepared.transferId) != CartCleanupResult.Complete
        }
        val bets = (prepared as? CartTransferPreparation.Ready)?.bets.orEmpty()
        if (!isCurrent(identity)) throw AccountVerificationSessionExpiredException()
        val response = remote.verify(identity, bets)
        check(response.code == null && response.validationErrors.isNullOrEmpty()) {
            "O serviço não confirmou a verificação de cadastro"
        }
        val access = accountAccess(response.payload, response.redirect)
        val received = prepared is CartTransferPreparation.Ready && response.redirect == null && response.payload?.cart != null
        if (prepared is CartTransferPreparation.Ready && !received) reviewRequired = true
        val content = if (response.redirect == null) response.payload?.let { payload ->
            AuthenticatedSessionContent(
                cart = payload.cart?.toDomainCart(),
                latestNotification = payload.latestNotification?.let {
                    AccountNotification(it.id, it.title, it.content, it.imageBase64, it.link)
                },
                unidentifiedPayment = payload.unidentifiedPayment == true,
                localCartReviewRequired = reviewRequired,
            )
        } else null
        if (!sessionContent.publish(identity, content)) throw AccountVerificationSessionExpiredException()
        if (received) {
            if (!isCurrent(identity)) throw AccountVerificationSessionExpiredException()
            check(transfer!!.confirmReceived(identity.ownerKey, prepared.transferId)) { "Recibo de transferência indisponível" }
            if (!isCurrent(identity)) throw AccountVerificationSessionExpiredException()
            if (transfer.finishConfirmed(identity.ownerKey, prepared.transferId) != CartCleanupResult.Complete) {
                if (!sessionContent.publish(identity, content?.copy(localCartReviewRequired = true))) throw AccountVerificationSessionExpiredException()
            }
        }
        return access
    }
}
