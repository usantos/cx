package br.gov.caixa.loterias.feature.home.presentation.route

import br.gov.caixa.loterias.feature.home.R as LocalizedR

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import br.gov.caixa.loterias.feature.home.presentation.components.LegacyHomeEasing
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.feature.home.presentation.components.QUICK_ACCESS_ANIMATION_DURATION_MILLIS
import br.gov.caixa.loterias.feature.home.presentation.components.QuickAccessContentOffset

internal data class HomePanelAnimations(
    val carouselOffset: androidx.compose.ui.unit.Dp,
    val cloverRotation: Float,
    val quickAccessToggleEnabled: Boolean,
    val campaignRotation: Float,
    val footerScrimAlpha: Float,
)

@Composable
internal fun homePanelAnimations(
    quickAccessExpanded: Boolean,
    octoberRosePanelVisible: Boolean,
    quickAccessOffset: Dp = QuickAccessContentOffset,
    campaignContentHeight: Dp = 0.dp,
): HomePanelAnimations {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var lastPanelWasQuick by remember { mutableStateOf(false) }
    SideEffect {
        if (quickAccessExpanded) lastPanelWasQuick = true
        if (octoberRosePanelVisible) lastPanelWasQuick = false
    }
    val closingDelay = if (!quickAccessExpanded && !octoberRosePanelVisible &&
        lastPanelWasQuick
    ) {
        2000
    } else if (!quickAccessExpanded && !octoberRosePanelVisible) {
        500
    } else {
        0
    }
    var settledPanel by remember { mutableStateOf(false to false) }
    LaunchedEffect(quickAccessExpanded, octoberRosePanelVisible) {
        val duration = when {
            quickAccessExpanded -> 1300L
            !octoberRosePanelVisible && lastPanelWasQuick -> 2500L
            octoberRosePanelVisible -> 700L
            else -> 1000L
        }
        if (duration > 0L) delay(duration)
        settledPanel = quickAccessExpanded to octoberRosePanelVisible
    }
    val carouselOffset by animateDpAsState(
        targetValue = when {
            octoberRosePanelVisible -> campaignContentHeight + 16.dp
            quickAccessExpanded -> quickAccessOffset
            else -> 0.dp
        },
        animationSpec = tween(
            durationMillis = QUICK_ACCESS_ANIMATION_DURATION_MILLIS,
            easing = LegacyHomeEasing,
            delayMillis = closingDelay
        ),
        label = uiStrings.getString(LocalizedR.string.ui_home_carousel_offset_324d54)
    )
    val cloverRotation by animateFloatAsState(
        targetValue = if (quickAccessExpanded) 450f else 90f,
        animationSpec = tween(
            durationMillis = QUICK_ACCESS_ANIMATION_DURATION_MILLIS,
            easing = LegacyHomeEasing,
            delayMillis = closingDelay
        ),
        label = uiStrings.getString(LocalizedR.string.ui_home_clover_rotation_604b21)
    )
    val campaignRotation by animateFloatAsState(
        targetValue = if (octoberRosePanelVisible) 360f else 0f,
        animationSpec = tween(QUICK_ACCESS_ANIMATION_DURATION_MILLIS, delayMillis = closingDelay, easing = LegacyHomeEasing),
        label = uiStrings.getString(LocalizedR.string.ui_home_campaign_rotation_a55a88)
    )
    val footerScrimAlpha by animateFloatAsState(
        targetValue = if (quickAccessExpanded) .8f else 0f,
        animationSpec = tween(QUICK_ACCESS_ANIMATION_DURATION_MILLIS, delayMillis = closingDelay, easing = LegacyHomeEasing),
        label = uiStrings.getString(LocalizedR.string.ui_home_footer_scrim_5de357)
    )
    return HomePanelAnimations(
        carouselOffset,
        cloverRotation,
        settledPanel == (quickAccessExpanded to octoberRosePanelVisible),
        campaignRotation,
        footerScrimAlpha
    )
}
