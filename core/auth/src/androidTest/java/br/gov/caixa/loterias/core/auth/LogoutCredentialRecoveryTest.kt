package br.gov.caixa.loterias.core.auth

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.lang.reflect.Proxy
import java.util.UUID
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import br.gov.caixa.loterias.core.auth.gateway.OidcConfiguration
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationDeviceInfoProvider
import br.gov.caixa.loterias.core.auth.authorization.WeakBiometricAvailability
import br.gov.caixa.loterias.core.auth.authorization.AuthorizationRequestPolicy
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.auth.gateway.AppAuthAuthenticationGateway

@RunWith(AndroidJUnit4::class)
class LogoutCredentialRecoveryTest {
    @Test fun failedBiometricClearRetainsIdentityForRetryThenClearsCredentials() {
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        var failWrites = true
        val context = object : ContextWrapper(base) {
            override fun getApplicationContext(): Context = this
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
                val preferences = base.getSharedPreferences(name, mode)
                return object : SharedPreferences by preferences {
                    override fun edit(): SharedPreferences.Editor {
                        val editor = preferences.edit()
                        return Proxy.newProxyInstance(SharedPreferences.Editor::class.java.classLoader,
                            arrayOf(SharedPreferences.Editor::class.java)) { proxy, method, arguments ->
                            if (method.name == "commit" && failWrites) false
                            else method.invoke(editor, *(arguments ?: emptyArray())).let {
                                if (it === editor) proxy else it
                            }
                        } as SharedPreferences.Editor
                    }
                }
            }
        }
        val tokens = SessionTokenStore()
        tokens.completeAuthorization(tokens.beginAuthorization(), "synthetic-access", "synthetic-id", "a".repeat(64))
        val identity = requireNotNull(tokens.captureIdentity())
        val gateway = AppAuthAuthenticationGateway(context, OidcConfiguration(
            authorizationEndpoint = "https://example.invalid/auth", tokenEndpoint = "https://example.invalid/token",
            clientId = "test", redirectUri = "test:/auth", endSessionEndpoint = "https://example.invalid/logout",
            postLogoutRedirectUri = "test:/logout", sessionNamespace = "logout-test-${UUID.randomUUID()}",
        ), tokens, AuthorizationRequestPolicy(AuthorizationDeviceInfoProvider { null }, WeakBiometricAvailability { false }))
        assertTrue(runCatching { gateway.logout() }.isFailure)
        assertSame(identity, tokens.captureIdentity())
        assertEquals("synthetic-access", tokens.token())
        failWrites = false
        gateway.logout()
        assertNull(tokens.captureIdentity())
        assertNull(tokens.token())
        gateway.logout() // A recuperação local pode ser repetida com segurança após a confirmação da transação.
        assertNull(tokens.token())
    }
}
