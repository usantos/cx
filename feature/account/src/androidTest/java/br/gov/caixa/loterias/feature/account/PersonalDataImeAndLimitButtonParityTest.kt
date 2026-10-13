package br.gov.caixa.loterias.feature.account

import android.view.View
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.account.*
import java.math.BigDecimal
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Installed APK layout contract. Synthetic identity and scalar limits; no account writes. */
class PersonalDataImeAndLimitButtonParityTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var contentView: View
    private val requestLabel = "Solicitar limite acima de R$ 9.300,00"

    @Test fun decimalKeyboardDoesNotLiftUpdateFooterAboveIme() {
        showPersonalData()
        compose.onNodeWithText("Atualizar").assertIsDisplayed()
        compose.onNode(hasSetTextAction() and hasText("R$ 1.050,00"))
            .performScrollTo().performClick().assertIsFocused()
        compose.waitUntil(10_000) { imeBottom() > 0 }
        compose.waitForIdle()
        val footer = compose.onAllNodes(hasText("Atualizar") and hasClickAction()).fetchSemanticsNodes()
        val screen = requireNotNull(InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot())
        val screenHeight = screen.height
        screen.recycle()
        val location = IntArray(2)
        var keyboardTop = 0
        compose.runOnIdle {
            contentView.getLocationOnScreen(location)
            keyboardTop = screenHeight - imeBottom()
        }
        assertTrue("Installed DadosPessoais hides Atualizar behind IME; footer must not float above it",
            footer.isEmpty() || footer.all { it.boundsInRoot.top + location[1] >= keyboardTop })
    }

    @Test fun requestLimitControlUsesInstalledGreyFill() {
        showPersonalData()
        val button = compose.onNode(hasText(requestLabel) and hasClickAction())
        button.performScrollTo().assertIsDisplayed().assertIsEnabled()
        val pixels = button.captureToImage().toPixelMap()
        // Empty interior above the label: official screenshot RGB 214/215/215, away from stroke/text.
        val sample = pixels[pixels.width / 2, (8 * contentView.resources.displayMetrics.density).toInt()]
        assertTrue("Installed request-limit button has grey fill, not white outlined-button interior: $sample",
            abs(sample.red * 255 - 214) <= 3 && abs(sample.green * 255 - 215) <= 3 && abs(sample.blue * 255 - 215) <= 3)
    }

    @Test fun requestLimitLabelFitsOneLineWithInstalledHorizontalPadding() {
        showPersonalData()
        compose.onNode(hasText(requestLabel) and hasClickAction()).performScrollTo()
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(requestLabel, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals("Expected one request-limit text layout", 1, layouts.size)
        // APK: 15sp Caixa regular, paddingStart/End 12dp; same public 9300 bound and device width.
        assertEquals("Installed request-limit label fits one line at the reference physical width", 1, layouts.single().lineCount)
    }

    private fun imeBottom(): Int = ViewCompat.getRootWindowInsets(contentView)
        ?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 0

    private fun showPersonalData() {
        val model = PersonalDataViewModel(
            getProfile = GetAccountProfileUseCase { AppResult.Success(AccountProfile(
                "00000000000", "Pessoa de Teste", "teste@example.invalid", "00000000", Gender.MALE,
                MunicipalityId(1, 1, 1), "DF", "Município de Teste", BigDecimal("1050"),
                BigDecimal("2"), BigDecimal("9300"), false)) },
            resolveZipCode = ResolveZipCodeUseCase { AppResult.Failure(AppError.Network) },
            updatePersonalData = UpdatePersonalDataUseCase { _, _ -> error("No account writes allowed") },
            recalculateAuthorizedLimit = RecalculateAuthorizedLimitUseCase { error("No recalculation request allowed") },
        )
        compose.setContent {
            val view = LocalView.current
            SideEffect { contentView = view }
            CaixaDSTheme(darkTheme = false) { PersonalDataRoute(model, {}, { error("No save completion allowed") }) }
        }
        compose.waitUntil { !model.state.value.loading }
    }
}
