package br.gov.caixa.loterias.feature.account.domain.port

/** Preferências e capacidades de login lembrado; a troca de protocolo permanece na fronteira da plataforma. */
interface BiometricLoginPreferences {
    val biometricEnabled: Boolean
    val biometricHardwareAvailable: Boolean get() = true
    val biometricAvailable: Boolean
    val biometricCredentialAvailable: Boolean
    val biometricFirstName: String? get() = null
    fun setBiometricEnabled(enabled: Boolean)
}
