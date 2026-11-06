package br.gov.caixa.loterias.core.persistence

import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import java.io.File
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Usa apenas um novo namespace de fixture no diretório privado do aplicativo de instrumentação. */
class AndroidSecureStoresTest {
    @Test fun realKeystoreRoundTripKeepsKeyNonExportableAndDiskEncrypted() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().context
        val label = "fixture_" + UUID.randomUUID().toString().replace("-", "")
        val scope = SecureStoreScope("installation", "guest", label)
        val store = AndroidSecureStores(context).open(scope)
        val sentinel = "CONTROLLED_TEST_SELECTION_1987"
        val committed = store.update { mapOf("selection" to sentinel) }
        assertEquals(committed, AndroidSecureStores(context).open(scope).read())
        val directory = File(context.noBackupFilesDir, "secure_datastore")
        assertTrue(directory.isDirectory)
        assertFalse(directory.walkTopDown().filter(File::isFile).any { it.readText().contains(sentinel) })
        val provider = AndroidSecureKeyProvider()
        val alias = "secure_test_" + UUID.randomUUID().toString()
        try {
            val key = provider.get(alias, true)
            assertEquals(null, key.encoded)
            assertEquals(key, provider.get(alias, false))
        } finally {
            java.security.KeyStore.getInstance("AndroidKeyStore").apply { load(null) }.deleteEntry(alias)
        }
    }
}
