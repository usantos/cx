package br.gov.caixa.loterias.core.security.authentication

import android.content.Context
import androidx.biometric.BiometricManager
import br.com.topaz.heartbeat.Heartbeat
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationDeviceInfoProvider
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationRequestPolicy
import br.gov.caixa.loterias.core.auth.authorization.WeakBiometricAvailability

class TopazAuthorizationDeviceInfoProvider(private val context: Context) : AuthorizationDeviceInfoProvider {
    override fun deviceId(): String? = Heartbeat.getInfo(context, 8001)
}

class AndroidWeakBiometricAvailability(private val context: Context) : WeakBiometricAvailability {
    override fun available(): Boolean = BiometricManager.from(context)
        .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
}

/** Simulações nunca invocam os fornecedores da plataforma nem fabricam um identificador de produção. */
fun loginAuthorizationRequestPolicy(
    simulated: Boolean,
    deviceInfo: AuthorizationDeviceInfoProvider,
    weakBiometrics: WeakBiometricAvailability,
): AuthorizationRequestPolicy = if (simulated) {
    AuthorizationRequestPolicy(AuthorizationDeviceInfoProvider { null }, WeakBiometricAvailability { false })
} else AuthorizationRequestPolicy(deviceInfo, weakBiometrics)
