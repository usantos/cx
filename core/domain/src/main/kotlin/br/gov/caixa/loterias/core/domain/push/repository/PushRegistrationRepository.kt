package br.gov.caixa.loterias.core.domain.push.repository

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import java.security.MessageDigest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun interface PushRegistrationRepository {
    suspend fun register(identity: AuthenticatedSessionIdentity, cpf: Long, token: String): Boolean
}
