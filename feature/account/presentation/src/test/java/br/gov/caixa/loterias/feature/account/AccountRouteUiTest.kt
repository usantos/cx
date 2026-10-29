// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.account

import android.content.ActivityNotFoundException
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.presentation.route.AccountRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.AccountStatusViewModel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AccountRouteUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `confirmation routes show distinct destinations without opening browser`() {
        val registration = mutableStateOf(true)
        val scheduler = TestCoroutineScheduler()
        val dispatchers = FixedCoroutineDispatchers(
            StandardTestDispatcher(scheduler)
        )
        var completions = 0
        compose.setContent {
            CaixaDSTheme {
                AccountRoute(
                    androidx.compose.runtime.remember(registration.value) {
                        AccountStatusViewModel(
                            if (registration.value) "account/register-confirmation" else "account/personal-data-confirmation",
                            dispatchers
                        )
                    },
                    {},
                    { completions++ },
                    "https://account.invalid"
                )
            }
        }
        compose.onNodeWithText("Excelente! Seu cadastro foi realizado com sucesso!").assertExists()
        compose.onNodeWithText("Aguarde apenas um instante enquanto te redirecionamos para suas apostas.").assertExists()
        compose.runOnIdle {
            scheduler.advanceTimeBy(3_001)
            scheduler.runCurrent()
            Snapshot.sendApplyNotifications()
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, completions)
            registration.value = false
            Snapshot.sendApplyNotifications()
        }
        compose.onNodeWithText("Seu cadastro foi atualizado com sucesso.").assertExists()
        compose.onNodeWithText("Aguarde apenas um instante enquanto te redirecionamos para a tela inicial.").assertExists()
        compose.runOnIdle {
            scheduler.advanceTimeBy(3_001)
            scheduler.runCurrent()
            Snapshot.sendApplyNotifications()
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(2, completions) }
    }

    @Test
    @Config(sdk = [35], qualifiers = "w411dp-h891dp-xhdpi")
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    fun `personal data confirmation centers legacy header and shows success content`() {
        val model = AccountStatusViewModel(
            "account/personal-data-confirmation",
            FixedCoroutineDispatchers(StandardTestDispatcher(TestCoroutineScheduler()))
        )
        compose.setContent { CaixaDSTheme { AccountRoute(model, {}, {}, "https://account.invalid") } }
        val title = compose.onNodeWithText("Loterias Caixa").fetchSemanticsNode().boundsInRoot
        val screen = compose.onRoot().fetchSemanticsNode().boundsInRoot
        assertEquals(screen.center.x, title.center.x, 1f)
        compose.onNodeWithText("Seu cadastro foi atualizado com sucesso.").assertExists()
        compose.onNodeWithText("Aguarde apenas um instante enquanto te redirecionamos para a tela inicial.").assertExists()
        java.io.File("/tmp/simlo-registration-success.png").outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @Test
    @Config(sdk = [35], qualifiers = "w360dp-h800dp-xhdpi")
    @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    fun `success message wraps fully with larger font on a narrow screen`() {
        val model = AccountStatusViewModel(
            "account/personal-data-confirmation",
            FixedCoroutineDispatchers(StandardTestDispatcher(TestCoroutineScheduler()))
        )
        compose.setContent {
            val density = androidx.compose.ui.platform.LocalDensity.current
            CompositionLocalProvider(
                androidx.compose.ui.platform.LocalDensity provides
                    androidx.compose.ui.unit.Density(density.density, 1.3f)
            ) {
                CaixaDSTheme { AccountRoute(model, {}, {}, "https://account.invalid") }
            }
        }
        val message = compose.onNodeWithText("Seu cadastro foi atualizado com sucesso.")
        val layouts = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        message.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        org.junit.Assert.assertTrue("Must exercise multiline text", layout.lineCount > 1)
        org.junit.Assert.assertFalse("Success message must not be clipped", layout.hasVisualOverflow)
        org.junit.Assert.assertTrue(layout.getLineBottom(layout.lineCount - 1) <= message.fetchSemanticsNode().boundsInRoot.height)
        java.io.File("/tmp/simlo-success-wrapped.png").outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @Test fun `missing browser offers retry and opens exact configured account uri once`() = browserFailure(ActivityNotFoundException())

    @Test fun `browser security failure can be retried locally`() = browserFailure(SecurityException())

    @Test fun `invalid uri failure can be retried locally`() = browserFailure(IllegalArgumentException())
    private fun browserFailure(failure: RuntimeException) {
        val attempts = mutableListOf<String>()
        var back = 0
        val url = "https://account.invalid/profile?source=synthetic"
        val handler = object : UriHandler {
            override fun openUri(uri: String) {
                attempts += uri
                if (attempts.size == 1) throw failure
            }
        }
        val model = AccountStatusViewModel("account/change-login")
        compose.setContent {
            CompositionLocalProvider(LocalUriHandler provides handler) { CaixaDSTheme { AccountRoute(model, { back++ }, {}, url) } }
        }
        compose.onNodeWithText("Não foi possível abrir a página segura da Conta CAIXA. Tente novamente.").assertExists()
        assertEquals(listOf(url), attempts)
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("A página segura da Conta CAIXA foi aberta no navegador.").assertExists()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(listOf(url, url), attempts)
            assertEquals(1, back)
        }
    }
}
