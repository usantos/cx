package br.gov.caixa.loterias.core.auth.authorization

fun interface AuthorizationDeviceInfoProvider { fun deviceId(): String? }
fun interface WeakBiometricAvailability { fun available(): Boolean }

class AuthorizationRequestOptions(val scope: String?, val additionalParameters: Map<String, String>) {
    override fun toString() = "AuthorizationRequestOptions(redacted)"
}

/** Política exata de requisição de AcessoBO/TopazUtil, separada do login biométrico compatível com PIN. */
class AuthorizationRequestPolicy(
    private val deviceInfo: AuthorizationDeviceInfoProvider,
    private val weakBiometrics: WeakBiometricAvailability,
) {
    fun capture(): AuthorizationRequestOptions {
        val weakAvailable = try { weakBiometrics.available() } catch (_: Exception) { false }
        val scope = if (weakAvailable) "openid profile offline_access" else null
        val digest = try { deviceInfo.deviceId() } catch (_: Exception) { null }
        val params = if (digest.isNullOrEmpty()) emptyMap() else mapOf(
            "deviceid" to digest.replace('|', '-'),
            "nivel" to "10",
            "app" to "br.gov.caixa.loterias.apostas",
            "origem" to "tofd",
        )
        return AuthorizationRequestOptions(scope, params)
    }
}
