package br.gov.caixa.loterias.feature.cart.data.datasource.local

import android.content.Context
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import java.io.IOException
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive

class CartRecoveryRequiredException : IOException("Legacy cart requires ownership and environment review")

/** Captura a identidade uma vez e rejeita um resultado se o vínculo de sessão mudar durante a suspensão do IO de disco. */
internal class SecureCartAccess(
    private val stores: SecureStoreFactory,
    private val scope: () -> SecureStoreScope,
    private val beforeAccess: suspend () -> Unit = {},
    private val captureLease: () -> (() -> Boolean) = { { true } },
) {
    suspend fun <T> use(action: suspend (SecureCartDocument) -> T): T {
        val leaseCurrent = captureLease()
        val captured = scope()
        beforeAccess()
        ensureCurrent(captured, leaseCurrent)
        val result = action(SecureCartDocument(stores.open(captured)))
        ensureCurrent(captured, leaseCurrent)
        return result
    }
    private suspend fun ensureCurrent(captured: SecureStoreScope, leaseCurrent: () -> Boolean) {
        coroutineContext.ensureActive()
        if (!leaseCurrent() || scope() != captured) throw CancellationException("Cart persistence scope changed")
    }
}

internal class AndroidCartPersistence(context: Context, val scope: SecureStoreScope) {
    private val application = context.applicationContext
    private val stores = AndroidSecureStores(application)
    val document = SecureCartDocument(stores.open(scope))
    private val quarantine = SecureCartDocument(stores.open(SecureStoreScope("installation", "installation", "cart-legacy-v1")))
    private val migration = CartDocumentMigration(quarantine, UnassignedCartSource(application))
    private val favorites =
        SecureFavoriteUsageMigration(
            stores.open(SecureStoreScope("installation", "installation", "cart-legacy-v1")),
            UnassignedFavoriteUsageSource(application)
        )
    suspend fun migrateLegacy() {
        migration.migrate()
        favorites.migrate()
    }
    suspend fun requiresLegacyReview(): Boolean {
        migrateLegacy()
        return quarantine.requiresCartRecovery()
    }
    suspend fun requireReady() {
        if (requiresLegacyReview()) throw CartRecoveryRequiredException()
    }
}
