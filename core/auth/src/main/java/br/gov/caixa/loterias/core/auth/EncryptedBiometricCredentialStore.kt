package br.gov.caixa.loterias.core.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal class BiometricCredential(val refreshToken: String, val ownerKey: String, val nonce: String) {
    override fun toString() = "BiometricCredential(redacted)"
}

/** Only refresh credentials persist; access tokens and authenticated screen state remain volatile. */
internal class EncryptedBiometricCredentialStore(context: Context, namespace: String) {
    private val suffix = MessageDigest.getInstance("SHA-256").digest(namespace.toByteArray()).joinToString("") {
        (it.toInt() and 255).toString(16).padStart(2, '0')
    }
    private val alias = "loterias.biometric.$suffix"
    private val preferences = context.getSharedPreferences("loterias_biometric_$suffix", Context.MODE_PRIVATE)
    val enabled: Boolean get() = preferences.getBoolean("enabled", false)
    fun setEnabled(enabled: Boolean) {
        val editor = preferences.edit().putBoolean("enabled", enabled)
        if (!enabled) editor.remove("credential")
        check(editor.commit()) { "Não foi possível atualizar a biometria" }
        if (!enabled) deleteKey()
    }
    fun read(): BiometricCredential? {
        if (!enabled) return null
        val encoded = preferences.getString("credential", null) ?: return null
        return try {
            val envelope = JSONObject(encoded)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key(false), GCMParameterSpec(128, Base64.decode(envelope.getString("iv"), Base64.NO_WRAP)))
            cipher.updateAAD(alias.toByteArray())
            val payload = JSONObject(String(cipher.doFinal(Base64.decode(envelope.getString("data"), Base64.NO_WRAP)), Charsets.UTF_8))
            BiometricCredential(payload.getString("refresh"), payload.getString("owner"), payload.getString("nonce")).also {
                check(it.refreshToken.isNotBlank() && it.ownerKey.matches(Regex("[a-f0-9]{64}")) && it.nonce.isNotBlank())
            }
        } catch (_: Exception) {
            // Restore, key invalidation or corrupted ciphertext always falls back to browser login.
            clearCredential()
            null
        }
    }
    fun write(credential: BiometricCredential) {
        check(enabled)
        val payload = JSONObject().put("refresh", credential.refreshToken).put("owner", credential.ownerKey).put("nonce", credential.nonce)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key(true))
        cipher.updateAAD(alias.toByteArray())
        val envelope = JSONObject().put("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .put("data", Base64.encodeToString(cipher.doFinal(payload.toString().toByteArray(Charsets.UTF_8)), Base64.NO_WRAP))
        check(preferences.edit().putString("credential", envelope.toString()).commit()) { "Não foi possível salvar a biometria" }
    }
    fun clearCredential() {
        check(preferences.edit().remove("credential").commit()) { "Não foi possível encerrar a biometria" }
        deleteKey()
    }
    private fun deleteKey() {
        KeyStore.getInstance("AndroidKeyStore").apply { load(null); deleteEntry(alias) }
    }
    private fun key(create: Boolean): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(alias, null) as? SecretKey)?.let { return it }
        check(create) { "Credencial indisponível" }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build())
        }.generateKey()
    }
}
