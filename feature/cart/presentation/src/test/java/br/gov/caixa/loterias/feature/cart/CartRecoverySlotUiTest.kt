package br.gov.caixa.loterias.feature.cart

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSPrimaryButton
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.navigation.CartRecoveryStatusPresentation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CartRecoverySlotUiTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun `recovery slot forwards loading network and retry while Back is gated by loading`() {
        val loading = mutableStateOf(true)
        var retries = 0
        var back = 0
        var dismisses = 0
        val fixture = CartWiringFixture()
        val observedLoading = mutableListOf<Boolean>()
        val adapter = fixture.adapter(object : CartRecoveryStatusPresentation {
            @Composable override fun Content(loading: Boolean, onRetry: () -> Unit, networkAvailability: NetworkAvailability) {
                SideEffect {
                    observedLoading += loading
                    assertSame(fixture.network, networkAvailability)
                }
                CaixaDSPrimaryButton("LOCAL RETRY", onClick = onRetry)
            }
        })
        compose.setContent {
            CaixaDSTheme {
                adapter.SimulationRecoveryPanel(loading.value, null, { retries++ }, { back++ }, { dismisses++ }, fixture.network)
            }
        }
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("LOCAL RETRY").performClick()
        compose.runOnIdle {
            assertEquals(0, back)
            assertEquals(1, retries)
            loading.value = false
            Snapshot.sendApplyNotifications()
        }
        compose.waitForIdle()
        compose.runOnIdle {
            compose.activity.onBackPressedDispatcher.onBackPressed()
            assertEquals(1, back)
            assertEquals(0, dismisses)
            assertTrue(observedLoading.containsAll(listOf(true, false)))
        }
    }

    @Test fun `recovery service message acknowledgment only invokes the injected dismiss callback`() {
        val message = mutableStateOf<String?>("Synthetic restore failed")
        var retries = 0
        var back = 0
        var dismisses = 0
        val fixture = CartWiringFixture()
        val adapter = fixture.adapter()
        compose.setContent {
            CaixaDSTheme {
                adapter.SimulationRecoveryPanel(false, message.value, { retries++ }, { back++ }, {
                    dismisses++
                    message.value =
                        null
                }, fixture.network)
            }
        }
        compose.onNodeWithText("Synthetic restore failed").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Synthetic restore failed").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, dismisses)
            assertEquals(0, retries)
            assertEquals(0, back)
        }
    }
}
