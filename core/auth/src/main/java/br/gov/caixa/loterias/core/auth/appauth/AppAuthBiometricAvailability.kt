package br.gov.caixa.loterias.core.auth.appauth

import android.content.Context
import br.gov.caixa.loterias.core.auth.gateway.biometricHardwareAvailable

internal class AppAuthBiometricAvailability(private val appContext: Context) {
    // USE_BIOMETRIC é declarado pelo app; o Lint da biblioteca não consegue ver seu manifesto.
    // A verificação de permissão em execução abaixo também nega o acesso quando outro hospedeiro omite a permissão.
    @get:android.annotation.SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    val biometricAvailable: Boolean get() {
        val keyguard = appContext.getSystemService(android.app.KeyguardManager::class.java)
        if (android.os.Build.VERSION.SDK_INT < 29) return keyguard.isDeviceSecure
        if (appContext.checkSelfPermission(android.Manifest.permission.USE_BIOMETRIC) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) return false
        val manager = appContext.getSystemService(android.hardware.biometrics.BiometricManager::class.java)
        return if (android.os.Build.VERSION.SDK_INT >= 30) manager.canAuthenticate(
            android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK or
                android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL,
        ) == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS
        else keyguard.isDeviceSecure || manager.canAuthenticate() == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS
    }
    @get:android.annotation.SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    val biometricHardwareAvailable: Boolean get() {
        val keyguard = appContext.getSystemService(android.app.KeyguardManager::class.java)
        if (android.os.Build.VERSION.SDK_INT < 29) {
            if (keyguard.isDeviceSecure) return true
            return appContext.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_FINGERPRINT)
        }
        if (appContext.checkSelfPermission(android.Manifest.permission.USE_BIOMETRIC) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) return false
        if (android.os.Build.VERSION.SDK_INT == 29 && keyguard.isDeviceSecure) return true
        val manager = appContext.getSystemService(android.hardware.biometrics.BiometricManager::class.java)
        val status = if (android.os.Build.VERSION.SDK_INT >= 30) manager.canAuthenticate(
            android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK or
                android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            else manager.canAuthenticate()
        return biometricHardwareAvailable(status)
    }
}
