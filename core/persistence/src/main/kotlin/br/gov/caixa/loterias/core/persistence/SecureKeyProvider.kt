package br.gov.caixa.loterias.core.persistence

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

internal fun interface SecureKeyProvider {
    fun get(alias: String, create: Boolean): SecretKey
}

internal class AndroidSecureKeyProvider : SecureKeyProvider {
    override fun get(alias: String, create: Boolean): SecretKey = synchronized(KEY_LOCK) {
        try {
            val store = KeyStore.getInstance(PROVIDER).apply { load(null) }
            val existing = store.getKey(alias, null)
            if (existing != null) {
                existing as? SecretKey ?: throw SecureStorageException.KeyUnavailable()
            } else {
                if (!create) throw SecureStorageException.KeyUnavailable()
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER).apply {
                    init(
                        KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setKeySize(AES_KEY_BITS)
                            .setRandomizedEncryptionRequired(true)
                            .build()
                    )
                }.generateKey()
            }
        } catch (failure: GeneralSecurityException) {
            throw SecureStorageException.KeyUnavailable(failure)
        }
    }

    private companion object {
        const val AES_KEY_BITS = 256
        const val PROVIDER = "AndroidKeyStore"
        val KEY_LOCK = Any()
    }
}
