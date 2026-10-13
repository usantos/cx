package br.gov.caixa.loterias.core.auth

import android.app.Activity
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import java.util.concurrent.atomic.AtomicBoolean

internal const val BIOMETRIC_STATE = "br.gov.caixa.loterias.biometric.STATE"

/** Platform prompt in the existing Compose activity, without a secondary activity or Fragment. */
// The app owns USE_BIOMETRIC; the guard verifies it before the annotated native call.
@android.annotation.SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
fun showBiometricPrompt(activity: Activity, challenge: Intent, onResult: (Intent?) -> Unit): () -> Unit {
    if (Build.VERSION.SDK_INT < 29) throw IllegalStateException("Confirme o bloqueio de tela do aparelho")
    if (activity.checkSelfPermission(android.Manifest.permission.USE_BIOMETRIC) !=
        android.content.pm.PackageManager.PERMISSION_GRANTED) {
        onResult(null)
        return {}
    }
    return showNativeBiometricPrompt(activity, challenge, onResult)
}

@androidx.annotation.RequiresApi(29)
@androidx.annotation.RequiresPermission(android.Manifest.permission.USE_BIOMETRIC)
@Suppress("DEPRECATION")
private fun showNativeBiometricPrompt(activity: Activity, challenge: Intent, onResult: (Intent?) -> Unit): () -> Unit {
    val state = checkNotNull(challenge.getStringExtra(BIOMETRIC_STATE)) { "Tentativa biométrica inválida" }
    val completed = AtomicBoolean(false)
    val cancellation = CancellationSignal()
    val builder = BiometricPrompt.Builder(activity).setTitle("Loterias Caixa").setDescription("Desbloqueie seu celular")
    if (Build.VERSION.SDK_INT >= 30) builder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
    else builder.setDeviceCredentialAllowed(true)
    builder.setConfirmationRequired(false).build().authenticate(cancellation, activity.mainExecutor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                if (completed.compareAndSet(false, true)) onResult(Intent().putExtra(BIOMETRIC_STATE, state))
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (completed.compareAndSet(false, true)) onResult(null)
            }
        })
    return {
        completed.set(true)
        cancellation.cancel()
    }
}
