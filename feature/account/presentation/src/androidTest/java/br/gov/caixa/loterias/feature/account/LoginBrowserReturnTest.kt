package br.gov.caixa.loterias.feature.account

import android.app.Activity
import android.content.Intent
import android.os.ParcelFileDescriptor
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSButton
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.presentation.route.LoginAuthorization
import br.gov.caixa.loterias.feature.account.presentation.route.rememberLoginAuthorization
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class LoginBrowserReturnTest {
    @get:Rule val compose = createAndroidComposeRule<androidx.activity.ComponentActivity>()

    @Test fun networkFailureCanReturnFromRealBrowserToExistingLoginHost() = verifyReturn(waitForBrowser = true)

    @Test fun networkLossDuringBrowserLaunchAlsoReturnsToExistingHost() = verifyReturn(waitForBrowser = false)

    private fun verifyReturn(waitForBrowser: Boolean) {
        val service = AuthorizationService(InstrumentationRegistry.getInstrumentation().targetContext)
        var network = ConnectionAvailability.CONNECTED
        var exchanges = 0
        var browserRequests = 0
        val auth = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent {
                browserRequests++
                return service.getAuthorizationRequestIntent(
                    AuthorizationRequest.Builder(
                        AuthorizationServiceConfiguration(
                            "https://example.invalid/authorize".toUri(),
                            "https://example.invalid/token".toUri()
                        ),
                        "test-client",
                        ResponseTypeValues.CODE,
                        "test-login:/callback".toUri()
                    ).build()
                )
            }
            override suspend fun completeAuthorization(result: Intent?) {
                exchanges++
            }
            override fun cancelAuthorization() = Unit
            override fun logout() = Unit
        }
        val model = LoginViewModel(auth, verifyAccess = VerifyPostLoginAccessUseCase { error("No account request") })
        lateinit var controller: LoginAuthorization
        try {
            compose.setContent {
                CaixaDSTheme {
                    controller = rememberLoginAuthorization(
                        model,
                        auth,
                        false,
                        {},
                        { _, _, _ -> {} },
                        NetworkAvailability { network },
                        null
                    )
                    if (controller.offline) {
                        CaixaDSText("OFFLINE LOGIN")
                    } else {
                        CaixaDSButton(onClick = controller.authorize) { CaixaDSText("BROWSER LOGIN") }
                    }
                }
            }
            compose.onNodeWithText("BROWSER LOGIN").performClick()
            compose.waitUntil(15_000) {
                if (waitForBrowser) {
                    chromeIsForeground()
                } else {
                    !compose.activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                }
            }
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                network = ConnectionAvailability.DISCONNECTED
                controller.connectionLost()
            }
            compose.waitUntil(15_000) { compose.activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) }
            compose.onNodeWithText("OFFLINE LOGIN").assertIsDisplayed()
            assertEquals(0, exchanges)
            assertFalse(model.state.value.authenticated)
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                network = ConnectionAvailability.CONNECTED
                controller.retry()
            }
            compose.waitUntil(15_000) { browserRequests == 2 }
            compose.waitUntil(15_000) {
                chromeIsForeground()
            }
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                network = ConnectionAvailability.DISCONNECTED
                controller.connectionLost()
            }
            compose.waitUntil(15_000) { compose.activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) }
            compose.onNodeWithText("OFFLINE LOGIN").assertIsDisplayed()
        } finally {
            service.dispose()
        }
    }

    private fun chromeIsForeground(): Boolean {
        if (compose.activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return false
        // No Android 16, a árvore de acessibilidade pode ficar nula mesmo com o Chrome visível.
        // O foco do WindowManager confirma o navegador real sem depender do conteúdo da página ou do primeiro uso.
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("dumpsys window displays")
        return ParcelFileDescriptor.AutoCloseInputStream(descriptor).bufferedReader().use { reader ->
            reader.lineSequence().any { line ->
                line.trimStart().startsWith("mCurrentFocus=") && line.contains(" com.android.chrome/")
            }
        }
    }
}
