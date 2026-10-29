// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.account

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.environment.model.EffectiveEnvironment
import br.gov.caixa.loterias.core.domain.environment.model.TestEnvironment
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration
import br.gov.caixa.loterias.core.navigation.state.AppSessionState
import br.gov.caixa.loterias.feature.account.domain.environment.ChangeEnvironment
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedUser
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RecalculateAuthorizedLimitUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.navigation.AccountEnvironmentDependencies
import br.gov.caixa.loterias.feature.account.navigation.AccountNavigationDependenciesAdapter
import br.gov.caixa.loterias.feature.account.navigation.LoginConnectionPresentation
import br.gov.caixa.loterias.feature.account.navigation.accountGraph
import br.gov.caixa.loterias.feature.account.presentation.route.AccountLoginEnvironmentSelector
import java.io.IOException
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AccountNavigationGraphUiTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var nav: NavHostController
    private val opened = mutableListOf<String>()
    private var profiles = 0
    private val authentication = object : AuthenticationGateway {
        override fun createAuthorizationIntent(): Intent = error("No browser login requested")
        override suspend fun completeAuthorization(result: Intent?) = error("No authorization callback")
        override fun cancelAuthorization() = Unit
        override fun logout() = Unit
    }
    private val session = object : SessionNavigationActions {
        override val state = AppSessionState()
        override val identity = AuthenticatedSessionIdentity("a".repeat(64))
        override val privateEntryKey = "synthetic-owner"
        override val isPrivateSessionCurrent = { true }
        override val onLogout = {}
        override val requestVisitorLogin: (String) -> Unit = { error("No private visitor action") }
        override val recheckAccountCompletion = {}
        override val onAuthenticated: (String) -> Unit = {}
        override val onTryNow = {}
    }
    private fun environment(visible: Boolean = false, change: suspend (TestEnvironment) -> Boolean = { error("No change expected") }) =
        object : AccountEnvironmentDependencies {
            override val effectiveEnvironment: EffectiveEnvironment get() = error("Picker must not resolve endpoints")
            override val selectedEnvironment = TestEnvironment.EXTERNO
            override val selectorVisible = visible
            override val changeEnvironment = ChangeEnvironment { change(it) }
        }
    private fun dependencies() = AccountNavigationDependenciesAdapter(
        loginNetworkAvailability = NetworkAvailability { ConnectionAvailability.CONNECTED },
        loginConnectionPresentation = object : LoginConnectionPresentation {
            @Composable override fun Offline(onRetry: () -> Unit, loading: Boolean) {
                CaixaDSText("OFFLINE FIXTURE")
            }
        },
        accountProfile = GetAccountProfileUseCase {
            profiles++
            AppResult.Success(
                AccountProfile(
                    "12345678901", "Synthetic Person", "fixture@example.test", "70000000", Gender.MALE,
                    MunicipalityId(7, 1, 0), "DF", "Brasília", BigDecimal("100"), BigDecimal.TEN, BigDecimal("200"), false
                )
            )
        },
        authentication = authentication,
        currentUserIdentity = CurrentUserIdentityProvider {
            AuthenticatedUser("Synthetic Person", "12345678901", "fixture@example.test", "")
        },
        dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
        verifyPostLoginAccess = VerifyPostLoginAccessUseCase { error("No login requested") },
        accountEnvironment = environment(),
        hostConfiguration = HostFeatureConfiguration(
            "1.2.3", "android-app://synthetic", "https://identity.invalid/account", "synthetic-client", true, null, true,
            "app",
            "key",
            "project",
            "sender",
            "https://backend.invalid/",
            "https://identity.invalid/realm",
            "https://identity.invalid/auth",
            "https://identity.invalid/token",
            "https://identity.invalid/logout", "synthetic://callback", false
        ),
        accountRegistrationDependencies = br.gov.caixa.loterias.feature.account.navigation.AccountRegistrationDependencies(
            recalculateAuthorizedLimit = RecalculateAuthorizedLimitUseCase { AppResult.Success(BigDecimal("200")) },
            registerAccount = RegisterAccountUseCase {
                error("Registration requires explicit submit")
            },
            resolveZipCode = ResolveZipCodeUseCase { AppResult.Success(ZipCodeLocation(MunicipalityId(7, 1, 0), "DF", "Brasília")) },
            updatePersonalData = UpdatePersonalDataUseCase { _, _ ->
                error("Update requires explicit submit")
            }
        )
    )
    private fun show() {
        val dependencies = dependencies()
        compose.setContent {
            CompositionLocalProvider(
                LocalUriHandler provides object : UriHandler {
                    override fun openUri(uri: String) {
                        opened += uri
                    }
                }
            ) {
                CaixaDSTheme {
                    nav = rememberNavController()
                    NavHost(nav, "home") {
                        composable("home") { CaixaDSText("HOME FIXTURE") }
                        composable("tickets/my-bets") { CaixaDSText("MY BETS FIXTURE") }
                        composable("support/terms") { CaixaDSText("TERMS FIXTURE") }
                        accountGraph(dependencies, nav, session)
                    }
                }
            }
        }
    }
    private fun open(route: String) {
        compose.runOnIdle { nav.navigate(route) { popUpTo("home") } }
        flush()
    }
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }

    @Test fun `typed account graph loads personal data and encodes external account return uri`() {
        show()
        open("account/personal-data")
        compose.onNodeWithText("Dados Pessoais").assertExists()
        compose.onNodeWithText("Synthetic Person").assertExists()
        compose.runOnIdle { assertEquals(1, profiles) }
        compose.onNodeWithContentDescription("Voltar").performClick()
        flush()
        compose.onNodeWithText("HOME FIXTURE").assertExists()
        open("account/change-login")
        compose.onNodeWithText("A página segura da Conta CAIXA foi aberta no navegador.").assertExists()
        compose.runOnIdle {
            assertEquals(
                listOf("https://identity.invalid/account?referrer=synthetic-client&referrer_uri=android-app%3A%2F%2Fsynthetic"),
                opened
            )
        }
    }

    @Test fun `typed graph registers login and registration without starting authentication or submission`() {
        show()
        open("account/register")
        compose.onNodeWithText("Que bom ver você por aqui").assertExists()
        compose.onNodeWithText("Não sei o meu CEP").performScrollTo().performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.runOnIdle { assertTrue(opened.isEmpty()) }
        compose.onNodeWithText("Não sei o meu CEP").performScrollTo().performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle {
            assertEquals(listOf("https://buscacepinter.correios.com.br/app/endereco/index.php"), opened)
            opened.clear()
        }
        open("account/login?authorize=false")
        compose.onNodeWithText("1.2.3", substring = true).assertExists()
        compose.runOnIdle {
            assertTrue(opened.isEmpty())
            assertEquals(0, profiles)
        }
    }

    @Test fun `environment selector retries a failed persisted change and safe host acknowledges closure`() {
        val requested = mutableListOf<TestEnvironment>()
        val dependencies = environment(true) {
            requested += it
            if (requested.size ==
                1
            ) {
                throw IOException("Synthetic persistence failure")
            }
            true
        }
        compose.setContent {
            CompositionLocalProvider(LocalContext provides RuntimeEnvironment.getApplication()) {
                CaixaDSTheme {
                    AccountLoginEnvironmentSelector(true, dependencies)
                }
            }
        }
        compose.onNodeWithTag("environment-picker").performClick()
        compose.onNodeWithText("INTERNO").performClick()
        flush()
        compose.onNodeWithText("Não foi possível salvar a mudança de ambiente. Tente novamente.").assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        flush()
        compose.onNodeWithText("O aplicativo será encerrado para aplicar as mudanças de ambiente.").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(listOf(TestEnvironment.INTERNO, TestEnvironment.INTERNO), requested) }
    }
}
