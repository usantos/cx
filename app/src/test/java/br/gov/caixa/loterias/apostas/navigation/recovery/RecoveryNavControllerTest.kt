package br.gov.caixa.loterias.apostas.navigation.recovery

import android.app.Application
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.gov.caixa.loterias.apostas.HomeDrawerTestActivity
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [TEST_API_LEVEL], application = Application::class)
class RecoveryNavControllerTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun savedRapidListBackstackDoesNotReenterItsAutomaticGenerationAfterRestoration() {
        val restoration = StateRestorationTester(compose)
        var automaticEntries = 0
        lateinit var nav: NavHostController
        restoration.setContent {
            CaixaDSTheme {
                nav = rememberRecoveryNavController()
                NavHost(nav, startDestination = "home") {
                    composable("home") { Text("Home") }
                    composable("betting/rapid-list") {
                        LaunchedEffect(Unit) { automaticEntries++ }
                        Text("Rapid list")
                    }
                }
            }
        }
        compose.runOnIdle { nav.navigate("betting/rapid-list") }
        compose.runOnIdle {
            assertEquals(1, automaticEntries)
            assertEquals("betting/rapid-list", nav.currentDestination?.route)
        }
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle {
            assertEquals("home", nav.currentDestination?.route)
            assertEquals(1, automaticEntries)
        }
    }

    @Test fun savedPaymentSuccessNeverBecomesAnAcknowledgedSuccessAfterRestoration() {
        val restoration = StateRestorationTester(compose)
        lateinit var nav: NavHostController
        restoration.setContent {
            CaixaDSTheme {
                nav = rememberRecoveryNavController()
                NavHost(nav, startDestination = "home") {
                    composable("home") { Text("Home") }
                    composable("payment/success") { Text("Success") }
                }
            }
        }
        compose.runOnIdle { nav.navigate("payment/success") }
        compose.runOnIdle { assertEquals("payment/success", nav.currentDestination?.route) }
        restoration.emulateSavedInstanceStateRestore()
        compose.runOnIdle { assertEquals("home", nav.currentDestination?.route) }
    }
}

private const val TEST_API_LEVEL = 35
