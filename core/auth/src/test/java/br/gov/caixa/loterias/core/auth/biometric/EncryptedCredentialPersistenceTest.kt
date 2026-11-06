// Literais numéricos descrevem payloads sintéticos, níveis de API e limites esperados das fixtures.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.auth.biometric

import android.app.Application
import android.security.keystore.KeyGenParameterSpec
import br.gov.caixa.loterias.core.auth.biometric.BiometricCredential
import br.gov.caixa.loterias.core.auth.biometric.EncryptedBiometricCredentialStore
import java.io.InputStream
import java.io.OutputStream
import java.security.Key
import java.security.KeyStore
import java.security.KeyStoreSpi
import java.security.MessageDigest
import java.security.Provider
import java.security.SecureRandom
import java.security.Security
import java.security.cert.Certificate
import java.security.spec.AlgorithmParameterSpec
import java.util.Collections
import java.util.Date
import javax.crypto.KeyGenerator
import javax.crypto.KeyGeneratorSpi
import javax.crypto.SecretKey
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/** Interface de keystore em memória; criptografia/descriptografia usa a implementação AES-GCM real da JVM. */
class TestCredentialKeyStore : KeyStoreSpi() {
    override fun engineGetKey(alias: String, password: CharArray?): Key? = keys[alias]
    override fun engineDeleteEntry(alias: String) {
        keys.remove(alias)
    }
    override fun engineContainsAlias(alias: String) = keys.containsKey(alias)
    override fun engineAliases() = Collections.enumeration(keys.keys)
    override fun engineSize() = keys.size
    override fun engineIsKeyEntry(alias: String) = keys.containsKey(alias)
    override fun engineIsCertificateEntry(alias: String) = false
    override fun engineGetCertificate(alias: String): Certificate? = null
    override fun engineGetCertificateChain(alias: String): Array<Certificate>? = null
    override fun engineGetCertificateAlias(cert: Certificate): String? = null
    override fun engineGetCreationDate(alias: String): Date? = if (keys.containsKey(alias)) Date(0) else null
    override fun engineSetKeyEntry(alias: String, key: Key, password: CharArray?, chain: Array<Certificate>?) {
        keys[alias] =
            key as SecretKey
    }
    override fun engineSetKeyEntry(alias: String, key: ByteArray, chain: Array<Certificate>?) = throw UnsupportedOperationException()
    override fun engineSetCertificateEntry(alias: String, cert: Certificate) = throw UnsupportedOperationException()
    override fun engineLoad(stream: InputStream?, password: CharArray?) = Unit
    override fun engineStore(stream: OutputStream?, password: CharArray?) = Unit
    companion object {
        val keys = mutableMapOf<String, SecretKey>()
    }
}
class TestCredentialKeyGenerator : KeyGeneratorSpi() {
    private lateinit var alias: String
    override fun engineInit(random: SecureRandom?) = Unit
    override fun engineInit(keysize: Int, random: SecureRandom?) = Unit
    override fun engineInit(params: AlgorithmParameterSpec, random: SecureRandom?) {
        alias = (params as KeyGenParameterSpec).keystoreAlias
    }
    override fun engineGenerateKey(): SecretKey {
        check(!failGeneration) { "simulated keystore failure" }
        return KeyGenerator.getInstance("AES").apply { init(256) }.generateKey().also { TestCredentialKeyStore.keys[alias] = it }
    }
    companion object {
        var failGeneration = false
    }
}
class CredentialTestProvider : Provider("AndroidKeyStore", 1.0, "In-memory keystore boundary for credential tests") {
    init {
        put("KeyStore.AndroidKeyStore", TestCredentialKeyStore::class.java.name)
        put("KeyGenerator.AES", TestCredentialKeyGenerator::class.java.name)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class EncryptedCredentialPersistenceTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Before fun install() {
        Security.removeProvider("AndroidKeyStore")
        Security.addProvider(CredentialTestProvider())
        TestCredentialKeyStore.keys.clear()
        TestCredentialKeyGenerator.failGeneration =
            false
    }

    @After fun remove() {
        Security.removeProvider("AndroidKeyStore")
        TestCredentialKeyStore.keys.clear()
        TestCredentialKeyGenerator.failGeneration =
            false
    }
    private fun preferences(namespace: String) = context.getSharedPreferences(
        "loterias_biometric_" + MessageDigest.getInstance("SHA-256")
            .digest(namespace.toByteArray()).joinToString("") { "%02x".format(it.toInt() and 255) },
        0
    )
    private fun credential() = BiometricCredential("refresh-secret", "a".repeat(64), "nonce-secret", "João")

    @Test fun `credential persists as authenticated ciphertext and disable removes credential and key`() {
        val store = EncryptedBiometricCredentialStore(context, "session-a")
        assertFalse(store.enabled)
        assertNull(store.read())
        assertTrue(runCatching { store.write(credential()) }.isFailure)
        store.setEnabled(true)
        store.write(credential())
        val encoded = preferences("session-a").getString("credential", null)!!
        assertFalse(encoded.contains("refresh-secret"))
        assertFalse(encoded.contains("nonce-secret"))
        assertFalse(encoded.contains("João"))
        assertFalse(encoded.contains("a".repeat(64)))
        val restored = EncryptedBiometricCredentialStore(context, "session-a").read()!!
        assertEquals("refresh-secret", restored.refreshToken)
        assertEquals("a".repeat(64), restored.ownerKey)
        assertEquals("nonce-secret", restored.nonce)
        assertEquals("João", restored.firstName)
        assertEquals("BiometricCredential(redacted)", restored.toString())
        assertFalse(TestCredentialKeyStore.keys.isEmpty())
        store.setEnabled(false)
        assertFalse(store.enabled)
        assertNull(store.read())
        assertNull(preferences("session-a").getString("credential", null))
        assertTrue(TestCredentialKeyStore.keys.isEmpty())
    }

    @Test fun `tampered ciphertext and missing restored key clear only remembered credential`() {
        val store = EncryptedBiometricCredentialStore(context, "session-a")
        store.setEnabled(true)
        assertNull(store.read())
        store.write(credential())
        val envelope = JSONObject(preferences("session-a").getString("credential", null)!!)
        envelope.put("data", "AAAA")
        preferences("session-a").edit().putString("credential", envelope.toString()).commit()
        assertNull(store.read())
        assertTrue(store.enabled)
        assertTrue(TestCredentialKeyStore.keys.isEmpty())
        store.write(credential())
        TestCredentialKeyStore.keys.clear()
        assertNull(EncryptedBiometricCredentialStore(context, "session-a").read())
        assertNull(preferences("session-a").getString("credential", null))
    }

    @Test fun `namespace isolation refuses copied credentials and malformed payload identities`() {
        val first = EncryptedBiometricCredentialStore(context, "first")
        val second = EncryptedBiometricCredentialStore(context, "second")
        first.setEnabled(true)
        second.setEnabled(true)
        first.write(credential())
        second.write(credential())
        preferences("second").edit().putString("credential", preferences("first").getString("credential", null)).commit()
        assertNull(second.read())
        assertEquals("refresh-secret", first.read()!!.refreshToken)
        for (invalid in listOf(
            BiometricCredential("", "a".repeat(64), "nonce"),
            BiometricCredential("refresh", "invalid", "nonce"),
            BiometricCredential("refresh", "a".repeat(64), "")
        )) {
            first.write(invalid)
            assertNull(first.read())
            assertTrue(first.enabled)
        }
        preferences("first").edit().putString("credential", "bad json").commit()
        assertNull(first.read())
        first.clearCredential()
        assertNull(first.read())
    }
}
