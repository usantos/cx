package br.gov.caixa.loterias.feature.home

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Contratos de animação pertencentes à apresentação, independentes dos componentes do split. */
class HomePanelAnimationsUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun quickAccessLocksToggleAndDelaysReturnUntilFadeChainCompletes() {
        compose.mainClock.autoAdvance = false
        val expanded = androidx.compose.runtime.mutableStateOf(false)
        var motion: br.gov.caixa.loterias.feature.home.presentation.route.HomePanelAnimations? = null
        compose.setFeatureContent {
            motion =
                br.gov.caixa.loterias.feature.home.presentation.route.homePanelAnimations(expanded.value, false, QUICK_ACCESS_OFFSET.dp)
            androidx.compose.material3.Text("Animação")
        }
        compose.runOnIdle { expanded.value = true }
        compose.mainClock.advanceTimeByFrame()
        compose.runOnIdle { assertEquals(false, motion!!.quickAccessToggleEnabled) }
        compose.mainClock.advanceTimeBy(EXPANSION_SETTLED_MS)
        compose.runOnIdle { assertEquals(true, motion!!.quickAccessToggleEnabled) }
        compose.runOnIdle { expanded.value = false }
        compose.mainClock.advanceTimeBy(RETURN_OFFSET_REACHED_MS)
        compose.runOnIdle {
            assertEquals(QUICK_ACCESS_OFFSET, motion!!.carouselOffset.value, GEOMETRY_TOLERANCE)
            assertEquals(false, motion!!.quickAccessToggleEnabled)
        }
        compose.mainClock.advanceTimeBy(RETURN_SETTLED_MS)
        compose.runOnIdle {
            assertEquals(0f, motion!!.carouselOffset.value, GEOMETRY_TOLERANCE)
            assertEquals(true, motion!!.quickAccessToggleEnabled)
        }
    }

    @Test fun campaignUsesContentHeightWithoutMaskingCarouselFooter() {
        compose.mainClock.autoAdvance = false
        val campaign = androidx.compose.runtime.mutableStateOf(false)
        var motion: br.gov.caixa.loterias.feature.home.presentation.route.HomePanelAnimations? = null
        compose.setFeatureContent {
            motion =
                br.gov.caixa.loterias.feature.home.presentation.route.homePanelAnimations(
                    false,
                    campaign.value,
                    quickAccessOffset = CLOSED_CAMPAIGN_OFFSET.dp,
                    campaignContentHeight = CAMPAIGN_CONTENT_HEIGHT.dp
                )
            androidx.compose.material3.Text("Campanha")
        }
        compose.runOnIdle { campaign.value = true }
        compose.mainClock.advanceTimeBy(CAMPAIGN_EXPANSION_MS)
        compose.runOnIdle {
            assertEquals(EXPANDED_CAMPAIGN_OFFSET, motion!!.carouselOffset.value, GEOMETRY_TOLERANCE)
            assertEquals(FULL_ROTATION, motion!!.campaignRotation, GEOMETRY_TOLERANCE)
            assertEquals(0f, motion!!.footerScrimAlpha, ALPHA_TOLERANCE)
            assertEquals(true, motion!!.quickAccessToggleEnabled)
            campaign.value = false
        }
        compose.mainClock.advanceTimeBy(CAMPAIGN_COLLAPSE_PAUSE_MS)
        compose.runOnIdle {
            assertEquals(EXPANDED_CAMPAIGN_OFFSET, motion!!.carouselOffset.value, GEOMETRY_TOLERANCE)
            assertEquals(false, motion!!.quickAccessToggleEnabled)
        }
        compose.mainClock.advanceTimeBy(CAMPAIGN_COLLAPSE_SETTLED_MS)
        compose.runOnIdle {
            assertEquals(0f, motion!!.carouselOffset.value, GEOMETRY_TOLERANCE)
            assertEquals(0f, motion!!.campaignRotation, GEOMETRY_TOLERANCE)
            assertEquals(0f, motion!!.footerScrimAlpha, ALPHA_TOLERANCE)
            assertEquals(true, motion!!.quickAccessToggleEnabled)
        }
    }
}

private const val GEOMETRY_TOLERANCE = .5f

private const val QUICK_ACCESS_OFFSET = 500f
private const val CLOSED_CAMPAIGN_OFFSET = 600f
private const val CAMPAIGN_CONTENT_HEIGHT = 314f
private const val EXPANDED_CAMPAIGN_OFFSET = 330f
private const val FULL_ROTATION = 360f
private const val ALPHA_TOLERANCE = .01f
private const val EXPANSION_SETTLED_MS = 1600L
private const val RETURN_OFFSET_REACHED_MS = 2000L
private const val RETURN_SETTLED_MS = 1200L
private const val CAMPAIGN_EXPANSION_MS = 800L
private const val CAMPAIGN_COLLAPSE_PAUSE_MS = 450L
private const val CAMPAIGN_COLLAPSE_SETTLED_MS = 700L
