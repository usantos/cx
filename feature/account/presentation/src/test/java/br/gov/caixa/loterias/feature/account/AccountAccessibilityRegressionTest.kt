package br.gov.caixa.loterias.feature.account

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.contract.account.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.presentation.route.RegistrationRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.RegistrationViewModel
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@RunWith(DynamicFeatureRobolectricTestRunner::class)
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], application = android.app.Application::class)
class AccountAccessibilityRegressionTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    private fun showRegistration() {
        val vm = RegistrationViewModel(
            CurrentUserIdentityProvider { null },
            ResolveZipCodeUseCase { AppResult.Failure(AppError.Network) },
            RegisterAccountUseCase { AppResult.Success(Unit) },
            FixedCoroutineDispatchers(StandardTestDispatcher())
        )
        compose.setFeatureContent { LoteriasCaixaDSTheme { RegistrationRoute(vm, {}, {}, {}) } }
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
