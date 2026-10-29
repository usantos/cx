package br.gov.caixa.loterias.apostas

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.apostas.navigation.PostLoginShakeTutorial
import br.gov.caixa.loterias.apostas.navigation.PostLoginTutorialRequest
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionCoordinator
import br.gov.caixa.loterias.apostas.navigation.session.AppSessionDependencies
import br.gov.caixa.loterias.apostas.navigation.session.rememberAppSessionCoordinator
import br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionBackgroundState
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.gateway.EndSessionGateway
import br.gov.caixa.loterias.core.auth.session.SessionTokenStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSScaffold
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.state.rememberAppNavigationState
import br.gov.caixa.loterias.core.navigation.workflow.LoginNavigation
import br.gov.caixa.loterias.core.navigation.workflow.openVisitorDrawerDestination
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome
import br.gov.caixa.loterias.feature.account.domain.model.LocalSessionRecovery
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.ClearLocalSessionDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResumeLocalSessionCleanupUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSessionUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.navigation.AccountSessionDependencies
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Exercita o tutorial, as funções de retorno do visitante, o coordenador e o cabeçalho de produção; não troca credenciais. */
@RunWith(AndroidJUnit4::class)
class PostLoginTutorialNavigationUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private lateinit var nav: NavHostController
    private lateinit var coordinator: AppSessionCoordinator
    private val session = TestSession()
    private val settings = TutorialSettings()
    private val renderedAfterLogin = mutableListOf<String>()
    private val destination = "account/personal-data"

    @Test fun normalLoginSkipOpensHome() = scenario(false, Exit.SKIP)

    @Test fun normalLoginCloseOpensHome() = scenario(false, Exit.CLOSE)

    @Test fun normalLoginCompleteOpensHome() = scenario(false, Exit.COMPLETE)

    @Test fun visitorLoginSkipReturnsToFeature() = scenario(true, Exit.SKIP)

    @Test fun visitorLoginCloseReturnsToFeature() = scenario(true, Exit.CLOSE)

    @Test fun visitorLoginCompleteReturnsToFeature() = scenario(true, Exit.COMPLETE)

    private fun scenario(visitor: Boolean, exit: Exit) {
        showTutorialScenario()
        if (visitor) {
            compose.onNodeWithText("Experimente").performClick()
            compose.onNodeWithText("Home").assertIsDisplayed()
            compose.onNodeWithText("Abrir Dados Pessoais").performClick()
            compose.onNodeWithText("Sim").performClick()
            compose.runOnIdle {
                assertEquals(destination, coordinator.state.visitorReturnRoute)
                assertEquals(LoginNavigation.pattern, nav.currentDestination?.route)
            }
        }
        compose.onNodeWithText("Entrar com sessão de teste").performClick()
        compose.onNodeWithText("Pular tutorial").assertIsDisplayed()
        compose.runOnIdle {
            assertTrue("No authenticated screen may render before the tutorial exits", renderedAfterLogin.isEmpty())
            assertEquals(LoginNavigation.pattern, nav.currentDestination?.route)
        }
        if (exit != Exit.SKIP) {
            repeat(TUTORIAL_ADVANCE_COUNT) { compose.onNodeWithContentDescription("Próxima etapa").performClick() }
        }
        when (exit) {
            Exit.SKIP -> compose.onNodeWithText("Pular tutorial").performClick()
            Exit.CLOSE -> compose.onNodeWithText("Fechar tutorial").performClick()
            Exit.COMPLETE -> compose.onNodeWithContentDescription("Fechar tutorial").performClick()
        }
        val expected = if (visitor) destination else "home"
        compose.waitUntil(NAVIGATION_TIMEOUT_MILLIS) { nav.currentDestination?.route == expected }
        compose.onNodeWithText("Pular tutorial").assertDoesNotExist()
        compose.onNodeWithText("Fechar tutorial").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(listOf(expected), renderedAfterLogin)
            assertEquals(listOf(requireNotNull(session.lease).ownerKey), settings.completedOwners)
            assertNull(coordinator.state.visitorReturnRoute)
        }
        if (visitor) {
            compose.onNodeWithText("Funcionalidade acessada").assertIsDisplayed()
            compose.runOnIdle { assertEquals("home", nav.previousBackStackEntry?.destination?.route) }
            compose.onNodeWithContentDescription("Voltar").performClick()
            compose.onNodeWithText("Home").assertIsDisplayed()
        } else {
            compose.onNodeWithText("Home").assertIsDisplayed()
            compose.runOnIdle { assertNull(nav.previousBackStackEntry) }
        }
    }

    private fun showTutorialScenario() {
        val dependencies = AppSessionDependencies(
            session,
            ForegroundSessionBackgroundState(),
            Clock.fixed(Instant.ofEpochSecond(TEST_CLOCK_EPOCH_SECONDS), ZoneOffset.UTC)
        )
        val preferences = HomePreferencesUseCases(
            object : HomeLayoutRepository {
                override suspend fun loadOrder(): String? = null
                override suspend fun saveOrder(order: String) = Unit
            },
            settings
        )
        compose.setContent {
            nav = rememberNavController()
            val navigation = rememberAppNavigationState(session.lease?.ownerKey)
            var pending by remember { mutableStateOf<PostLoginTutorialRequest?>(null) }
            var validated by remember { mutableStateOf(false) }
            CaixaDSTheme {
                coordinator = rememberAppSessionCoordinator(dependencies, nav, navigation, session.lease, {})
                NavHost(nav, startDestination = LoginNavigation.pattern) {
                    composable(LoginNavigation.pattern) {
                        Column {
                            Button(onClick = {
                                session.lease = AuthenticatedSessionIdentity("a".repeat(OWNER_KEY_LENGTH))
                                validated = true
                                pending = PostLoginTutorialRequest("home", requireNotNull(session.lease))
                            }) { Text("Entrar com sessão de teste") }
                            Button(onClick = coordinator.onTryNow) { Text("Experimente") }
                        }
                    }
                    composable("home") {
                        SideEffect { if (validated) record("home") }
                        Column {
                            Text("Home")
                            Button(onClick = { openVisitorDrawerDestination(destination, nav, coordinator.requestVisitorLogin) }) {
                                Text("Abrir Dados Pessoais")
                            }
                        }
                    }
                    composable(destination) {
                        SideEffect { if (validated) record(destination) }
                        CaixaDSScaffold(title = "Dados Pessoais", onBack = { nav.popBackStack() }) { padding ->
                            Text("Funcionalidade acessada", Modifier.padding(padding))
                        }
                    }
                }
                pending?.let { request ->
                    PostLoginShakeTutorial(
                        request,
                        session.lease,
                        session.authenticatedSessionIdentity,
                        preferences,
                        session.dispatchers,
                        onReady = {
                            pending = null
                            coordinator.onAuthenticated(it)
                        },
                        onCancelled = { pending = null }
                    )
                }
            }
        }
    }

    private fun record(route: String) {
        if (renderedAfterLogin.lastOrNull() != route) renderedAfterLogin.add(route)
    }

    private enum class Exit { SKIP, CLOSE, COMPLETE }

    private class TutorialSettings : ShakeSettingsRepository {
        val completedOwners = mutableListOf<String?>()
        override suspend fun enabled() = false
        override suspend fun setEnabled(enabled: Boolean) = Unit
        override suspend fun warningHidden() = false
        override suspend fun setWarningHidden(hidden: Boolean) = Unit
        override suspend fun tutorialCompleted(ownerKey: String?) = ownerKey == null || ownerKey in completedOwners
        override suspend fun completeTutorial(ownerKey: String?) {
            completedOwners.add(ownerKey)
        }
        override suspend fun tooltipPending(ownerKey: String?) = false
        override suspend fun consumeTooltip(ownerKey: String?) = Unit
    }

    private class TestSession : AccountSessionDependencies {
        var lease by mutableStateOf<AuthenticatedSessionIdentity?>(null)
        private val tokens = SessionTokenStore()
        override val authenticatedSessionIdentity = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = lease
            override fun isCurrent(identity: AuthenticatedSessionIdentity) = lease === identity
        }
        override val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("No browser login allowed")
            override suspend fun completeAuthorization(result: Intent?) = error("No token exchange allowed")
            override fun cancelAuthorization() = Unit
            override fun logout() = error("No logout requested")
        }
        override val endSession = object : EndSessionGateway {
            override fun prepare() = EndSessionLaunch()
            override fun intentFor(launch: EndSessionLaunch): Intent = error("No browser operation allowed")
            override fun complete(launch: EndSessionLaunch, result: Intent?) = EndSessionOutcome.UNCONFIRMED
            override fun discard(launch: EndSessionLaunch) = Unit
        }
        override val clearLocalSessionData = ClearLocalSessionDataUseCase { error("No cleanup requested") }
        override val resumeLocalSessionCleanup = ResumeLocalSessionCleanupUseCase { LocalSessionRecovery.NONE_PENDING }
        override val signOutSession = SignOutSessionUseCase { error("No sign-out requested") }
        override val verifyPostLoginAccess = VerifyPostLoginAccessUseCase {
            PostLoginAccessResult.Verified(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.ZERO))
        }
        override val authenticatedSessionContent = tokens
        override val currentUserIdentity = CurrentUserIdentityProvider { null }
        override val dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate)
        override val sessionIdentityChanges = MutableStateFlow<AuthenticatedSessionIdentity?>(null)
        override val foregroundSession = tokens
    }
}

private const val TEST_CLOCK_EPOCH_SECONDS = 100L
private const val OWNER_KEY_LENGTH = 64
private const val TUTORIAL_ADVANCE_COUNT = 5
private const val NAVIGATION_TIMEOUT_MILLIS = 5_000L
