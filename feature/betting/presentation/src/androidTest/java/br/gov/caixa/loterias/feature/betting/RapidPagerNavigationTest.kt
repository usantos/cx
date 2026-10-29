package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidPageIndicator
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidPagerSynchronization

class RapidPagerNavigationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun summaryDotJumpBackToAmountSettlesWithoutIntermediatePageFeedback() {
        val requested = mutableIntStateOf(0)
        lateinit var pager: PagerState
        compose.setContent {
            pager = rememberPagerState(pageCount = { 7 })
            RapidPagerSynchronization(pager, requested.intValue) { requested.intValue = it }
            Column(Modifier.fillMaxSize()) {
                HorizontalPager(pager, Modifier.weight(1f).testTag("pages")) {
                    Box(Modifier.fillMaxSize())
                }
                RapidPageIndicator(requested.intValue) { requested.intValue = it }
            }
        }
        compose.onNodeWithContentDescription("Etapa 7 de 7").performTouchInput { click(center) }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(6, pager.settledPage)
            assertEquals(6, requested.intValue)
            assertEquals(0f, pager.currentPageOffsetFraction, 0.001f)
        }
        compose.onNodeWithContentDescription("Etapa 3 de 7").performTouchInput { click(center) }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(2, pager.settledPage)
            assertEquals(2, requested.intValue)
            assertEquals(0f, pager.currentPageOffsetFraction, 0.001f)
        }
        compose.onNodeWithTag("pages").performTouchInput { swipeLeft() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(3, pager.settledPage)
            assertEquals(3, requested.intValue)
            assertEquals(0f, pager.currentPageOffsetFraction, 0.001f)
        }
    }
}
