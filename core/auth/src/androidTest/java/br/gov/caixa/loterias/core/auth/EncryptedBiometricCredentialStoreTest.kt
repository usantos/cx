package br.gov.caixa.loterias.core.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore
import java.security.MessageDigest
import java.util.UUID

/** Exercises real Android Keystore/GCM and disk preferences with synthetic credentials only. */
@RunWith(AndroidJUnit4::class)
class EncryptedBiometricCredentialStoreTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val namespaces = mutableListOf<String>()
    private val credential = BiometricCredential("synthetic-refresh-only-for-test", "a".repeat(64), "synthetic-nonce")

    @After fun removeSyntheticCredentials() {
        namespaces.forEach { namespace ->
            EncryptedBiometricCredentialStore(context, namespace).setEnabled(false)
            check(preferences(namespace).edit().clear().commit())
        }
    }

    @Test fun roundTripSurvivesStoreRecreationAndRefreshRotation() {
        val namespace = namespace()
        val store = EncryptedBiometricCredentialStore(context, namespace)
        assertFalse(store.enabled)
        assertNull(store.read())
        store.setEnabled(true)
        store.write(credential)
        val firstEnvelope = checkNotNull(preferences(namespace).getString("credential", null))
        assertFalse(firstEnvelope.contains(credential.refreshToken))
        assertFalse(firstEnvelope.contains(credential.ownerKey))
        assertFalse(firstEnvelope.contains(credential.nonce))
        val recreated = EncryptedBiometricCredentialStore(context, namespace)
        assertTrue(recreated.enabled)
        assertCredential(credential, recreated.read())
        val rotated = BiometricCredential("synthetic-rotated-refresh", credential.ownerKey, credential.nonce)
        recreated.write(rotated)
        assertNotEquals(firstEnvelope, preferences(namespace).getString("credential", null))
        assertCredential(rotated, EncryptedBiometricCredentialStore(context, namespace).read())
    }

    @Test fun disablingRemovesDiskCredentialAndKeystoreKeyAndPreventsWrite() {
        val namespace = populatedNamespace()
        val store = EncryptedBiometricCredentialStore(context, namespace)
        store.setEnabled(false)
        assertFalse(EncryptedBiometricCredentialStore(context, namespace).enabled)
        assertNull(EncryptedBiometricCredentialStore(context, namespace).read())
        assertFalse(preferences(namespace).contains("credential"))
        assertFalse(keyStore().containsAlias(alias(namespace)))
        assertTrue(runCatching { store.write(credential) }.isFailure)
        assertFalse(keyStore().containsAlias(alias(namespace)))
    }

    @Test fun logoutStyleClearDeletesCredentialAndKeyButRetainsOptInPreference() {
        val namespace = populatedNamespace()
        EncryptedBiometricCredentialStore(context, namespace).clearCredential()
        val recreated = EncryptedBiometricCredentialStore(context, namespace)
        assertTrue(recreated.enabled)
        assertNull(recreated.read())
        assertFalse(preferences(namespace).contains("credential"))
        assertFalse(keyStore().containsAlias(alias(namespace)))
        recreated.write(credential)
        assertCredential(credential, recreated.read())
    }

    @Test fun independentNamespacesDoNotReadOrClearEachOthersCredential() {
        val first = populatedNamespace()
        val second = namespace()
        val secondStore = EncryptedBiometricCredentialStore(context, second)
        secondStore.setEnabled(true)
        assertNull(secondStore.read())
        val other = BiometricCredential("synthetic-other-environment-refresh", "b".repeat(64), "synthetic-other-nonce")
        secondStore.write(other)
        assertCredential(credential, EncryptedBiometricCredentialStore(context, first).read())
        assertCredential(other, EncryptedBiometricCredentialStore(context, second).read())
        secondStore.clearCredential()
        assertCredential(credential, EncryptedBiometricCredentialStore(context, first).read())
    }

    @Test fun copiedCiphertextFromAnotherNamespaceFailsClosedWithoutClearingOriginal() {
        val first = populatedNamespace()
        val second = populatedNamespace() // Has its own valid Keystore key.
        val firstEnvelope = preferences(first).getString("credential", null)
        check(preferences(second).edit().putString("credential", firstEnvelope).putString("unrelated", "keep").commit())
        assertInvalidCredentialCleared(second)
        assertCredential(credential, EncryptedBiometricCredentialStore(context, first).read())
    }

    @Test fun missingKeystoreKeyClearsCiphertextRetainsPreferenceAndAcceptsFutureLogin() {
        val namespace = populatedNamespace()
        check(preferences(namespace).edit().putString("unrelated", "keep").commit())
        keyStore().deleteEntry(alias(namespace))
        assertInvalidCredentialCleared(namespace)
        val recreated = EncryptedBiometricCredentialStore(context, namespace)
        recreated.write(credential)
        assertCredential(credential, recreated.read())
    }

    @Test fun tamperedGcmCiphertextCannotRestoreCredentialAndIsRemoved() {
        val namespace = populatedNamespace()
        val envelope = JSONObject(checkNotNull(preferences(namespace).getString("credential", null)))
        val ciphertext = Base64.decode(envelope.getString("data"), Base64.NO_WRAP)
        ciphertext[0] = (ciphertext[0].toInt() xor 1).toByte()
        envelope.put("data", Base64.encodeToString(ciphertext, Base64.NO_WRAP))
        check(preferences(namespace).edit().putString("credential", envelope.toString()).putString("unrelated", "keep").commit())
        assertInvalidCredentialCleared(namespace)
    }

    @Test fun malformedEnvelopeIsClearedWithoutChangingOptInOrOtherPreferences() {
        val namespace = populatedNamespace()
        check(preferences(namespace).edit().putString("credential", "invalid synthetic envelope").putString("unrelated", "keep").commit())
        assertInvalidCredentialCleared(namespace)
    }

    private fun assertInvalidCredentialCleared(namespace: String) {
        val recreated = EncryptedBiometricCredentialStore(context, namespace)
        assertNull(recreated.read())
        assertTrue(recreated.enabled)
        assertFalse(preferences(namespace).contains("credential"))
        assertFalse(keyStore().containsAlias(alias(namespace)))
        assertEquals("keep", preferences(namespace).getString("unrelated", null))
        assertNull(EncryptedBiometricCredentialStore(context, namespace).read())
    }
    private fun assertCredential(expected: BiometricCredential, actual: BiometricCredential?) {
        assertNotNull(actual)
        assertEquals(expected.refreshToken, actual!!.refreshToken)
        assertEquals(expected.ownerKey, actual.ownerKey)
        assertEquals(expected.nonce, actual.nonce)
    }
    private fun namespace(): String = "synthetic-auth-storage-${UUID.randomUUID()}".also(namespaces::add)
    private fun populatedNamespace(): String = namespace().also {
        EncryptedBiometricCredentialStore(context, it).apply { setEnabled(true); write(credential) }
    }
    private fun suffix(namespace: String): String = MessageDigest.getInstance("SHA-256").digest(namespace.toByteArray()).joinToString("") {
        (it.toInt() and 255).toString(16).padStart(2, '0')
    }
    private fun preferences(namespace: String): SharedPreferences =
        context.getSharedPreferences("loterias_biometric_${suffix(namespace)}", Context.MODE_PRIVATE)
    private fun alias(namespace: String) = "loterias.biometric.${suffix(namespace)}"
    private fun keyStore() = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
}
