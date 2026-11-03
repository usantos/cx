package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
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
class HomeShakeDrawerEffectTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun tutorialReturnWaitsForHomeAndOpensOnlyOnceAcrossRefreshes() {
        val ready = mutableStateOf(false)
        var opened = 0
        compose.setFeatureContent { HomeShakeDrawerEffect(pending = true, homeReady = ready.value) { opened++ } }
        compose.runOnIdle {
            assertEquals(0, opened)
            ready.value = true
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, opened)
            ready.value = false
        }
        compose.waitForIdle()
        compose.runOnIdle { ready.value = true }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, opened) }
    }

    @Test fun consumingThePendingTipBeforeHomeLoadsCancelsAutomaticOpening() {
        val pending = mutableStateOf(true)
        val ready = mutableStateOf(false)
        var opened = 0
        compose.setFeatureContent { HomeShakeDrawerEffect(pending.value, ready.value) { opened++ } }
        compose.runOnIdle { pending.value = false }
        compose.waitForIdle()
        compose.runOnIdle { ready.value = true }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(0, opened) }
    }
}
