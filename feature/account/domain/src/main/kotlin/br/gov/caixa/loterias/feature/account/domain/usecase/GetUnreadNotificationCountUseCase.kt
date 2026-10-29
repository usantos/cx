package br.gov.caixa.loterias.feature.account.domain.usecase

import java.io.IOException
import br.gov.caixa.loterias.feature.account.domain.model.UnreadNotificationCountUnavailableException
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.repository.UnreadNotificationCountRepository

class GetUnreadNotificationCountUseCase(
    private val repository: UnreadNotificationCountRepository,
    private val sessions: AuthenticatedSessionIdentityProvider,
) {
    suspend operator fun invoke(): Int {
        val identity = sessions.captureIdentity() ?: return 0
        if (!sessions.isCurrent(identity)) return 0
        val count = try {
            repository.getCount(identity)
        } catch (_: IOException) {
            return 0
        } catch (_: UnreadNotificationCountUnavailableException) {
            return 0
        }
        return if (sessions.isCurrent(identity)) count.coerceAtLeast(0) else 0
    }
}
