package br.gov.caixa.loterias.feature.account.data.repository

import android.content.Context
import br.gov.caixa.loterias.feature.account.domain.repository.LocalSessionDataRepository
import br.gov.caixa.loterias.feature.account.domain.repository.LocalSessionCleanupRecoveryRepository
import java.util.concurrent.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import br.gov.caixa.loterias.feature.account.data.datasource.local.SessionLocalDataSource
import br.gov.caixa.loterias.feature.account.data.datasource.local.SessionLocalDataSourceImpl

/** Compatibilidade de atualização com DadosUsuarioBO/DadosBiometriaBO, sem redefinir o banco de dados. */
class LocalSessionDataRepositoryImpl internal constructor(
    private val store: SessionLocalDataSource,
) : LocalSessionDataRepository, LocalSessionCleanupRecoveryRepository {
    private val mutex = Mutex()

    override suspend fun clear() = mutex.withLock { clearLocked() }

    override suspend fun resumePending(): Boolean = mutex.withLock {
        if (!store.isCleanupPending()) return@withLock false
        clearLocked()
        true
    }

    private suspend fun clearLocked() {
        // Confirma antes de tocar em qualquer armazenamento. A recuperação pode repetir com segurança todas as etapas seguintes.
        store.setCleanupPending(true)
        var complete = true
        // Tenta os dois armazenamentos independentes mesmo que um não possa ser gravado; a repetição é idempotente.
        for (clear in listOf(store::clearLoginFlag, store::clearIdentityAndBiometricRecords)) {
            try { clear() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { complete = false }
        }
        check(complete) { "Não foi possível limpar os dados locais da sessão" }
        store.setCleanupPending(false)
    }

    companion object {
        fun fromContext(context: Context): LocalSessionDataRepositoryImpl =
            LocalSessionDataRepositoryImpl(SessionLocalDataSourceImpl(context.applicationContext))
    }
}

