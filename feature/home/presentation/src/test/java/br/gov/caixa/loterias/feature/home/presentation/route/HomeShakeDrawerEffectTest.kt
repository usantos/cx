package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HomeShakeDrawerEffectTest {
    @get:Rule val compose = createComposeRule()

    @Test fun tutorialReturnWaitsForHomeAndOpensOnlyOnceAcrossRefreshes() {
        val ready = mutableStateOf(false)
        var opened = 0
        compose.setContent { HomeShakeDrawerEffect(pending = true, homeReady = ready.value) { opened++ } }
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
        compose.setContent { HomeShakeDrawerEffect(pending.value, ready.value) { opened++ } }
        compose.runOnIdle { pending.value = false }
        compose.waitForIdle()
        compose.runOnIdle { ready.value = true }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(0, opened) }
    }
}
