// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.account

import android.content.Context
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.core.network.api.SessionLogoutApi
import br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountVerificationSessionBinding
import br.gov.caixa.loterias.feature.account.di.LatestNotificationModule
import br.gov.caixa.loterias.feature.account.di.SessionLogoutModule
import br.gov.caixa.loterias.feature.account.di.SessionModule
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.LocalSessionRecovery
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.repository.LocalSessionCleanupRecoveryRepository
import br.gov.caixa.loterias.feature.account.domain.repository.LocalSessionDataRepository
import br.gov.caixa.loterias.feature.account.domain.usecase.ClearLocalSessionDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResumeLocalSessionCleanupUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSessionUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.navigation.AccountSessionDependenciesAdapter
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AccountSessionModuleContractTest {
    @Test fun `session aliases share the same authority and empty content observation`() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val store = SessionModule.tokenStore()
        val policy = SessionModule.authorizationRequestPolicy(context, configuration())
        assertNull(policy.capture().scope)
        assertTrue(policy.capture().additionalParameters.isEmpty())
        val gateway = SessionModule.appAuthGateway(context, store, policy, configuration())
        assertSame(gateway, SessionModule.authentication(gateway))
        assertSame(gateway, SessionModule.endSession(gateway))
        assertSame(gateway, SessionModule.regularSessionRefresh(gateway))
        assertSame(store, SessionModule.sessionIdentity(store))
        assertSame(store, SessionModule.sessionContent(store))
        assertSame(store, LatestNotificationModule.latestNotificationSession(store))
        assertNull(SessionModule.currentUserIdentity(store).currentUser())
        assertNull(SessionModule.observeSessionContent(SessionModule.sessionContent(store))().first())
        gateway.cancelAuthorization()
        assertNull(store.token())
        assertNull(store.captureIdentity())
    }

    @Test fun `provided local cleanup rejects unavailable Keystore while preserving original preferences`() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val preferences = context.getSharedPreferences("USUARIO_KEY", Context.MODE_PRIVATE)
        preferences.edit().clear().putBoolean("usuariologado", true).putString("unrelated", "retained").commit()
        context.getSharedPreferences("SESSION_CLEANUP_STATE", Context.MODE_PRIVATE).edit().clear().commit()
        val concrete = SessionModule.legacyLocalSessionData(context)
        assertSame(concrete, SessionModule.localSessionData(concrete))
        assertSame(concrete, SessionModule.localSessionCleanupRecovery(concrete))
        try {
            SessionModule.resumeLocalSessionCleanup(concrete)()
            fail("Missing Android Keystore must not release startup as if migration succeeded")
        } catch (_: SecureStorageException) {
            assertTrue(preferences.getBoolean("usuariologado", false))
        }
        try {
            SessionModule.clearLocalSessionData(concrete)()
            fail("Cleanup effects must wait for an encrypted pending marker")
        } catch (_: SecureStorageException) {
            assertTrue(preferences.getBoolean("usuariologado", false))
        }
        assertEquals("retained", preferences.getString("unrelated", null))
        assertFalse(context.getSharedPreferences("SESSION_CLEANUP_STATE", Context.MODE_PRIVATE).contains("pending"))
    }

    @Test fun `provided cleanup operations propagate recovery and clear failures instead of reporting success`() = runBlocking {
        var clears = 0
        val clear = SessionModule.clearLocalSessionData(
            LocalSessionDataRepository {
                clears++
                throw IllegalStateException("write rejected")
            }
        )
        try {
            clear()
            fail("Persistence failure must propagate")
        } catch (
            expected: IllegalStateException,
        ) {
            assertEquals("write rejected", expected.message)
        }
        assertEquals(1, clears)
        var resumes = 0
        val resume = SessionModule.resumeLocalSessionCleanup(
            LocalSessionCleanupRecoveryRepository {
                resumes++
                resumes == 1
            }
        )
        assertEquals(LocalSessionRecovery.RECOVERED, resume())
        assertEquals(LocalSessionRecovery.NONE_PENDING, resume())
        assertEquals(2, resumes)
    }

    @Test fun `typed host session adapter forwards operations and retains shared identity owner`() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val store = SessionModule.tokenStore()
        val gateway = SessionModule.appAuthGateway(
            context,
            store,
            SessionModule.authorizationRequestPolicy(context, configuration()),
            configuration()
        )
        var clears = 0
        var recoveries = 0
        var signOuts = 0
        var verifications = 0
        val clear = ClearLocalSessionDataUseCase { clears++ }
        val recovery = ResumeLocalSessionCleanupUseCase {
            recoveries++
            LocalSessionRecovery.NONE_PENDING
        }
        val identity = AuthenticatedSessionIdentity("a".repeat(64))
        val signOut = SignOutSessionUseCase {
            signOuts++
            SessionSignOutResult.Confirmed(identity)
        }
        val verify = VerifyPostLoginAccessUseCase {
            verifications++
            PostLoginAccessResult.BlockedAccountCheckFailed
        }
        val currentUser = SessionModule.currentUserIdentity(store)
        val dispatchers = FixedCoroutineDispatchers(
            Dispatchers.Main.immediate
        )
        val adapter =
            AccountSessionDependenciesAdapter(
                gateway,
                gateway,
                clear,
                recovery,
                signOut,
                verify,
                store,
                store,
                currentUser,
                dispatchers,
                store
            )
        assertSame(gateway, adapter.authentication)
        assertSame(gateway, adapter.endSession)
        assertSame(store, adapter.authenticatedSessionIdentity)
        assertSame(store, adapter.authenticatedSessionContent)
        assertSame(store, adapter.foregroundSession)
        assertSame(currentUser, adapter.currentUserIdentity)
        assertSame(dispatchers, adapter.dispatchers)
        assertNull(adapter.sessionIdentityChanges.first())
        assertNull(adapter.authenticatedSessionContent.observe().first())
        adapter.clearLocalSessionData()
        assertEquals(LocalSessionRecovery.NONE_PENDING, adapter.resumeLocalSessionCleanup())
        assertEquals(SessionSignOutResult.Confirmed(identity), adapter.signOutSession())
        assertEquals(
            PostLoginAccessResult.BlockedAccountCheckFailed,
            adapter.verifyPostLoginAccess()
        )
        assertEquals(listOf(1, 1, 1, 1), listOf(clears, recoveries, signOuts, verifications))
    }

    @Test fun `logout module binds captured identity and propagates synthetic transport confirmation and failure`() = runBlocking {
        val identity = AuthenticatedSessionIdentity("b".repeat(64))
        val store = SessionModule.tokenStore()
        val binding = SessionModule.accountVerificationSessionBinding(store)
        val bound = mutableListOf<AuthenticatedSessionIdentity>()
        val recordedBinding = AccountVerificationSessionBinding { captured ->
            bound += captured
            binding.bind(captured)
        }
        var calls = 0
        var fail = false
        val provider = SessionLogoutModule.provider()
        provider.replace(object : SessionLogoutApi {
            override suspend fun signOut(session: AccountVerificationSession): retrofit2.Response<okhttp3.ResponseBody> {
                calls++
                if (fail) throw IOException("Synthetic transport failure")
                return retrofit2.Response.success<okhttp3.ResponseBody>(null)
            }
        })
        val remote = SessionLogoutModule.remote(provider, recordedBinding)
        val repository = SessionLogoutModule.repository(remote)
        val identities = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = identity
            override fun isCurrent(candidate: AuthenticatedSessionIdentity) = candidate === identity
        }
        val signOut = SessionLogoutModule.signOut(identities, repository)
        assertEquals(SessionSignOutResult.Confirmed(identity), signOut())
        fail = true
        assertTrue(signOut() is SessionSignOutResult.Rejected)
        assertEquals(2, calls)
        assertEquals(listOf(identity, identity), bound)
        assertNull(store.token())
    }
    private fun configuration() = HostFeatureConfiguration(
        appVersionName = "1.2.3", accountReferrerUri = "android-app://synthetic", ssoAccountUrl = "https://identity.invalid/account",
        ssoClientId = "synthetic-client", environmentSelectorVisible = false, homeNavigationEnvironmentLabel = null,
        mockBackendEnabled = true,
        firebaseAppId = "synthetic",
        firebaseApiKey = "synthetic",
        firebaseProjectId = "synthetic",
        firebaseSenderId = "synthetic",
        backendApiBaseUrl = "https://backend.invalid/", backendSessionNamespace = "https://identity.invalid/realm",
        ssoAuthUrl = "https://identity.invalid/realm/protocol/openid-connect/auth",
        ssoTokenUrl = "https://identity.invalid/realm/protocol/openid-connect/token",
        ssoLogoutUrl = "https://identity.invalid/realm/protocol/openid-connect/logout",
        ssoRedirectUri = "synthetic://login",
        productionBuild = false
    )
}
