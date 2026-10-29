package br.gov.caixa.loterias.feature.notifications.data.repository

import android.content.Context
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.domain.push.repository.PushRegistrationLocalRepository
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity

/** Persiste só resumos do par completo titular capturado/ambiente; marcadores históricos ambíguos ficam intactos. */
class PushRegistrationLocalRepositoryImpl(
    context: Context,
    private val environmentKey: () -> String,
    private val isCurrent: (AuthenticatedSessionIdentity) -> Boolean,
    private val factory: SecureStoreFactory = AndroidSecureStores(context),
) : PushRegistrationLocalRepository {
    override suspend fun contains(identity: AuthenticatedSessionIdentity, marker: String): Boolean {
        require(marker.matches(DIGEST))
        val environment = environmentKey()
        if (!isCurrent(identity)) return false
        val stored = factory.open(SecureStoreScope(environment, identity.ownerKey, "push_registration")).read()
        if (!isCurrent(identity) || environmentKey() != environment) return false
        return stored.values["confirmed"] == marker
    }

    override suspend fun remember(identity: AuthenticatedSessionIdentity, marker: String) {
        require(marker.matches(DIGEST))
        val environment = environmentKey()
        requireCurrent(identity, environment)
        factory.open(SecureStoreScope(environment, identity.ownerKey, "push_registration")).update {
            requireCurrent(identity, environment)
            mapOf("confirmed" to marker)
        }
        requireCurrent(identity, environment)
    }

    private fun requireCurrent(identity: AuthenticatedSessionIdentity, environment: String) {
        if (!isCurrent(identity) || environmentKey() != environment) throw SecureStorageException.WriteFailure()
    }

    private companion object {
        val DIGEST = Regex("[a-f0-9]{64}")
    }
}
