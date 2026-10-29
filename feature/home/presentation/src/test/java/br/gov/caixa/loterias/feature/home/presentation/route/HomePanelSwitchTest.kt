package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HomePanelSwitchTest {
    @Test fun `campaign waits for all four quick rows and carousel to close`() = runTest {
        val quick = mutableStateOf(true)
        val campaign = mutableStateOf(false)
        launch { switchHomePanel(quick, campaign, true) }
        runCurrent()
        assertFalse(quick.value)
        assertFalse(campaign.value)
        advanceTimeBy(2499)
        runCurrent()
        assertFalse(campaign.value)
        advanceTimeBy(1)
        runCurrent()
        assertTrue(campaign.value)
    }
    @Test fun `quick access waits for campaign fade and translation`() = runTest {
        val quick = mutableStateOf(false)
        val campaign = mutableStateOf(true)
        launch { switchHomePanel(quick, campaign, false) }
        runCurrent()
        assertFalse(campaign.value)
        assertFalse(quick.value)
        advanceTimeBy(1000)
        runCurrent()
        assertTrue(quick.value)
    }
}
