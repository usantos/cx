package br.gov.caixa.loterias.core.auth.appauth

import android.content.Context
import android.util.Base64
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import br.gov.caixa.loterias.core.auth.token.TokenEndpointIdentity
import br.gov.caixa.loterias.core.auth.gateway.OidcConfiguration
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationRequestPolicy
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.auth.gateway.BiometricAttemptRegistry
import br.gov.caixa.loterias.core.auth.gateway.biometricHardwareAvailable
import br.gov.caixa.loterias.core.auth.biometric.BiometricCredential
import br.gov.caixa.loterias.core.auth.biometric.EncryptedBiometricCredentialStore
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationAttemptRegistry
import br.gov.caixa.loterias.core.auth.authorization.EndSessionRegistry

internal class AppAuthSession(
    context: Context,
    val configuration: OidcConfiguration,
    val tokenStore: SessionTokenStore,
    val requestPolicy: AuthorizationRequestPolicy,
    val monitor: Any,
    val logout: () -> Unit,
) {
    val appContext = context.applicationContext
    val biometricCredentials = EncryptedBiometricCredentialStore(appContext,
        "${configuration.sessionNamespace}|${configuration.expectedIssuer}|${configuration.clientId}")
    val biometricAttempts = BiometricAttemptRegistry(tokenStore)
    var biometricChallengeCredential: BiometricCredential? = null
    val service = AuthorizationService(context.applicationContext)
    val attempts = AuthorizationAttemptRegistry<AuthorizationRequest>(tokenStore)
    val endSessions = EndSessionRegistry<EndSessionRequest>()
    var logoutIdToken: String? = null
    val endpointIdentity = TokenEndpointIdentity { Base64.decode(it, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING) }
    private val availability = AppAuthBiometricAvailability(appContext)
    val biometricEnabled: Boolean get() = biometricCredentials.enabled
    val biometricAvailable: Boolean get() = availability.biometricAvailable
    val biometricHardwareAvailable: Boolean get() = availability.biometricHardwareAvailable
    val tokenUpdates = AppAuthTokenUpdates(this)
}
