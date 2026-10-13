package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.cart.*
import br.gov.caixa.loterias.core.domain.account.AccountAccess
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.account.PostLoginAccountRepository
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContentRepository
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionContent
import br.gov.caixa.loterias.core.domain.account.AccountNotification
import br.gov.caixa.loterias.core.network.AccountVerificationSessionExpiredException

class RemotePostLoginAccountRepository(
    private val remote: RetrofitPostLoginAccountDataSource,
    private val sessionContent: AuthenticatedSessionContentRepository,
    private val transfer: LegacyCartTransferRepository? = null,
    private val isCurrent: (AuthenticatedSessionIdentity) -> Boolean = { true },
) : PostLoginAccountRepository {
    override suspend fun verifyAccess(identity: AuthenticatedSessionIdentity): AccountAccess {
        if (!isCurrent(identity)) throw AccountVerificationSessionExpiredException()
        val prepared = transfer?.prepare(identity.ownerKey) ?: LegacyCartTransferPreparation.Empty
        var reviewRequired = prepared == LegacyCartTransferPreparation.RequiresReview
        if (prepared is LegacyCartTransferPreparation.CleanupPending) {
            if (!isCurrent(identity)) throw AccountVerificationSessionExpiredException()
            reviewRequired = transfer!!.finishConfirmed(identity.ownerKey, prepared.transferId) != LegacyCartCleanupResult.Complete
        }
        val bets = (prepared as? LegacyCartTransferPreparation.Ready)?.bets.orEmpty()
        if (!isCurrent(identity)) throw AccountVerificationSessionExpiredException()
        val response = remote.verify(identity, bets)
        check(response.code == null && response.validationErrors.isNullOrEmpty()) {
            "O serviço não confirmou a verificação de cadastro"
        }
        val access = accountAccess(response.payload, response.redirect)
        val received = prepared is LegacyCartTransferPreparation.Ready && response.redirect == null && response.payload?.cart != null
        if (prepared is LegacyCartTransferPreparation.Ready && !received) reviewRequired = true
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
        if (received && prepared is LegacyCartTransferPreparation.Ready) {
            if (!isCurrent(identity)) throw AccountVerificationSessionExpiredException()
            check(transfer!!.confirmReceived(identity.ownerKey, prepared.transferId)) { "Recibo de transferência indisponível" }
            if (!isCurrent(identity)) throw AccountVerificationSessionExpiredException()
            if (transfer.finishConfirmed(identity.ownerKey, prepared.transferId) != LegacyCartCleanupResult.Complete) {
                if (!sessionContent.publish(identity, content?.copy(localCartReviewRequired = true))) throw AccountVerificationSessionExpiredException()
            }
        }
        return access
    }
}
