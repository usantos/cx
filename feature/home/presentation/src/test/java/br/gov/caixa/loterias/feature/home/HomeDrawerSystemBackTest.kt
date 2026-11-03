package br.gov.caixa.loterias.feature.home

import androidx.activity.OnBackPressedCallback
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.home.presentation.route.HomeDrawerIdentity
import br.gov.caixa.loterias.feature.home.presentation.route.HomeExitConfirmation
import br.gov.caixa.loterias.feature.home.presentation.route.HomeNavigationDrawer
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
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
class HomeDrawerSystemBackTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun backClosesTheMenuBeforeTheExistingExitConfirmationCanBeShown() {
        var closes = 0
        var exits = 0
        var unhandledBacks = 0
        compose.activity.onBackPressedDispatcher.addCallback(
            compose.activity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    unhandledBacks++
                }
            }
        )
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                val drawer = rememberDrawerState(DrawerValue.Open)
                val scope = rememberCoroutineScope()
                HomeExitConfirmation(enabled = !drawer.isOpen) { exits++ }
                HomeNavigationDrawer(
                    drawerState = drawer,
                    navigationBarInset = 0f,
                    preferenceState = null,
                    preferencesViewModel = null,
                    homeState = HomeUiState(),
                    identity = HomeDrawerIdentity(),
                    onClose = {
                        closes++
                        scope.launch { drawer.close() }
                    },
                    onExternalFailure = {},
                    onOpen = {},
                    onLogout = {}
                ) {}
            }
        }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, closes)
            assertEquals(0, unhandledBacks)
            assertEquals(0, exits)
            compose.activity.onBackPressedDispatcher.onBackPressed()
        }
        compose.onNodeWithText("Deseja sair do aplicativo?").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, exits) }
    }
}
