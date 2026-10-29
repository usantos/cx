package br.gov.caixa.loterias.feature.account

import android.content.ActivityNotFoundException
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.account.presentation.route.AccountRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.AccountStatusViewModel

/** Exercita a rota real, substituindo apenas a fronteira do navegador externo. */
class AccountExternalLinkTest {
    @get:Rule val compose = createComposeRule()

    @Test fun successfulOpeningKeepsTheOriginalMessageAndDoesNotRepeatOnRecomposition() {
        val handler = RecordingUriHandler()
        val back = mutableStateOf<() -> Unit>({})
        show(handler, onBack = { back.value })
        compose.onNodeWithText(SUCCESS).assertIsDisplayed()
        compose.runOnIdle { back.value = {} }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(listOf(URL), handler.urls) }
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
    }

    @Test fun absentBrowserShowsFailureWithoutFalseSuccessAndRetriesTheOriginalUrl() {
        assertFailureAndRetry(ActivityNotFoundException("No browser"))
    }

    @Test fun deniedBrowserShowsFailureWithoutFalseSuccessAndRetriesTheOriginalUrl() {
        assertFailureAndRetry(SecurityException("Browser denied"))
    }

    @Test fun rejectedUriShowsFailureAndAllowsRetry() {
        assertFailureAndRetry(IllegalArgumentException("Cannot open URI"))
    }

    @Test fun cancellationDoesNotBecomeFailureOrSuccess() {
        val handler = RecordingUriHandler(CancellationException("Route left"))
        show(handler)
        compose.waitForIdle()
        compose.onNodeWithText(SUCCESS).assertDoesNotExist()
        compose.onNodeWithText(FAILURE).assertDoesNotExist()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf(URL), handler.urls) }
    }

    private fun assertFailureAndRetry(failure: RuntimeException) {
        val handler = RecordingUriHandler(failure)
        val back = mutableStateOf<() -> Unit>({})
        show(handler, onBack = { back.value })
        compose.onNodeWithText(FAILURE).assertIsDisplayed()
        compose.onNodeWithText(SUCCESS).assertDoesNotExist()
        compose.runOnIdle { back.value = {}; handler.failure = null }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(listOf(URL), handler.urls) }
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText(SUCCESS).assertIsDisplayed()
        compose.onNodeWithText(FAILURE).assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf(URL, URL), handler.urls) }
    }

    private fun show(handler: UriHandler, onBack: () -> (() -> Unit) = { {} }) {
        val model = AccountStatusViewModel("account/change-login")
        compose.setContent {
            CompositionLocalProvider(LocalUriHandler provides handler) {
                CaixaDSTheme(darkTheme = false) {
                    AccountRoute(model, onBack(), { error("No account completion expected") }, URL)
                }
            }
        }
    }

    private class RecordingUriHandler(var failure: RuntimeException? = null) : UriHandler {
        val urls = mutableListOf<String>()
        override fun openUri(uri: String) {
            urls += uri
            failure?.let { throw it }
        }
    }

    private companion object {
        const val URL = "https://conta.example.invalid/account?redirect=loterias&state=original"
        const val SUCCESS = "A página segura da Conta CAIXA foi aberta no navegador."
        const val FAILURE = "Não foi possível abrir a página segura da Conta CAIXA. Tente novamente."
    }
}
