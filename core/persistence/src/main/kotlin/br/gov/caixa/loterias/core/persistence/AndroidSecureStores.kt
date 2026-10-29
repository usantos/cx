package br.gov.caixa.loterias.core.persistence

import android.content.Context
import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/** DataStores criptografados pertencentes ao processo; os registros são deliberadamente excluídos dos backups do Android. */
class AndroidSecureStores(context: Context) : SecureStoreFactory {
    private val stores = FileSecureStores(
        File(context.applicationContext.noBackupFilesDir, "secure_datastore"),
        AndroidSecureKeyProvider(),
        PROCESS_SCOPE
    )

    override fun open(scope: SecureStoreScope): SecureRecordStore = stores.open(scope)

    private companion object {
        val PROCESS_SCOPE = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
