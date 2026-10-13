package br.gov.caixa.loterias.core.domain.account

import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import kotlinx.coroutines.flow.Flow

data class AccountNotification(
    val id: Long?,
    val title: String?,
    val content: String?,
    /** Legacy notification images are base64, not remote URLs. */
    val imageBase64: String?,
    val link: String?,
) {
    override fun toString() = "AccountNotification(redacted)"
}

data class AuthenticatedSessionContent(
    val cart: ShoppingCart?,
    val latestNotification: AccountNotification?,
    val unidentifiedPayment: Boolean,
    val localCartReviewRequired: Boolean = false,
) {
    override fun toString() = "AuthenticatedSessionContent(redacted)"
}

interface AuthenticatedSessionContentRepository {
    /** Publication and identity check must be atomic with login/logout/replacement. */
    fun publish(identity: AuthenticatedSessionIdentity, content: AuthenticatedSessionContent?): Boolean
    fun observe(): Flow<AuthenticatedSessionContent?>
}

fun interface ObserveAuthenticatedSessionContentUseCase {
    operator fun invoke(): Flow<AuthenticatedSessionContent?>
}

class ObserveAuthenticatedSessionContent(private val repository: AuthenticatedSessionContentRepository) :
    ObserveAuthenticatedSessionContentUseCase {
    override fun invoke() = repository.observe()
}
