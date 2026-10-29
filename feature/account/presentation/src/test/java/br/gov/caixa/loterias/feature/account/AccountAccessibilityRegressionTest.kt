package br.gov.caixa.loterias.feature.account

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.presentation.route.RegistrationRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.RegistrationViewModel
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AccountAccessibilityRegressionTest {
    @get:Rule val compose = createComposeRule()

    private fun showRegistration() {
        val vm = RegistrationViewModel(
            CurrentUserIdentityProvider { null },
            ResolveZipCodeUseCase { AppResult.Failure(AppError.Network) },
            RegisterAccountUseCase { AppResult.Success(Unit) },
            FixedCoroutineDispatchers(StandardTestDispatcher())
        )
        compose.setContent { CaixaDSTheme { RegistrationRoute(vm, {}, {}, {}) } }
    }

    @Test fun termsTextContainsAnAccessibleLink() {
        showRegistration()
        val node = compose.onNode(hasText("Li e concordo", substring = true)).performScrollTo().fetchSemanticsNode()
        val text = node.config[SemanticsProperties.Text]
        assertTrue(
            "Terms must expose LinkAnnotation, not an offset-only pointer gesture",
            text.any { it.getLinkAnnotations(0, it.length).isNotEmpty() }
        )
    }

    @Test fun notificationConsentHasOneLabeledToggleOwner() {
        showRegistration()
        compose.onNode(hasText("Aceito receber", substring = true) and isToggleable())
            .performScrollTo().performClick().assertIsOff()
    }
}
