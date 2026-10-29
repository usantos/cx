package br.gov.caixa.loterias.feature.betting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.presentation.components.RapidPagerSynchronization

class RapidPagerCallbacksTest {
    @get:Rule val compose = createComposeRule()

    @Test fun settledSwipeUsesReplacementCallbackWithoutReplacingPager() {
        val first = mutableListOf<Int>()
        val second = mutableListOf<Int>()
        val replace = mutableStateOf(false)
        lateinit var pager: PagerState
        compose.setContent {
            pager = rememberPagerState(pageCount = { 7 })
            RapidPagerSynchronization(pager, 0, if (replace.value) ({ second.add(it) }) else ({ first.add(it) }))
            HorizontalPager(pager, Modifier.fillMaxSize().testTag("pages")) { Box(Modifier.fillMaxSize()) }
        }
        compose.runOnIdle { first.clear(); replace.value = true }
        compose.waitForIdle()
        compose.onNodeWithTag("pages").performTouchInput { swipeLeft() }
        compose.waitForIdle()
        compose.runOnIdle {
            assertTrue(first.isEmpty())
            assertEquals(listOf(1), second)
            assertEquals(1, pager.settledPage)
        }
    }
}
