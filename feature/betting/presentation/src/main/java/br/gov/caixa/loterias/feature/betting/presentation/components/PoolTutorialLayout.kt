package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSRegularFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.mapper.tutorialInstructionDescription
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialDefinition
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialPage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlin.math.min

@Composable
internal fun PoolTutorialLayout(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    pager: PagerState,
    onClose: () -> Unit,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
    onPageSelected: (Int) -> Unit,
) {
    val density = LocalDensity.current
    val enlargedFont = density.fontScale > 1f
    var navigationHeight by remember { mutableIntStateOf(0) }
    val finalPage = state.page == definition.pages.lastIndex
    var leftIntroduction by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(state.page) {
        if (state.page != 0) leftIntroduction = true
    }
    val regular = TextStyle(
        fontFamily = LoteriasCaixaDSRegularFontFamily,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
    BoxWithConstraints(
        Modifier.fillMaxSize().background(LoteriasCaixaDSColorTokens.White)
            .windowInsetsPadding(WindowInsets.safeDrawing).legacyAutomationId("pool-tutorial-viewport")
    ) {
        val navigationInset = if (enlargedFont) with(density) { navigationHeight.toDp() } else 0.dp
        val pagerHeight = (maxHeight - IndicatorStripHeight - navigationInset).coerceAtLeast(0.dp)
        HorizontalPager(pager, Modifier.fillMaxWidth().height(pagerHeight).legacyAutomationId("pool-tutorial-pager")) { index ->
            PoolTutorialMedia(definition.pages[index], index, definition.pages.size, index == pager.currentPage)
        }
        PoolTutorialTopActions(state, definition, finalPage, regular, onClose, onSecondary)
        PoolTutorialPrimary(state, definition, finalPage, leftIntroduction, pagerHeight, onPrimary, onToggleDoNotShowAgain)
        if (enlargedFont) {
            AnimatedTutorialNavigation(
                state.page,
                definition.pages.lastIndex,
                onPageSelected,
                Modifier.align(Alignment.BottomCenter).padding(bottom = IndicatorStripHeight).onSizeChanged { navigationHeight = it.height }
            )
        }
        PoolTutorialIndicators(state, definition)
    }
}

@Composable
private fun PoolTutorialMedia(page: TutorialPage, index: Int, total: Int, active: Boolean) {
    BoxWithConstraints(
        Modifier.fillMaxSize().clearAndSetSemantics {
            if (active) {
                contentDescription = tutorialInstructionDescription(page, index, total)
                role = Role.Image
            }
        },
        contentAlignment = Alignment.Center
    ) {
        // centerInside do XML preserva a área original de 360 x 640 dp sem ampliá-la.
        val scale = min(1f, min(maxWidth.value / MEDIA_WIDTH, maxHeight.value / MEDIA_HEIGHT))
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset(page.media))
        LottieAnimation(
            composition,
            Modifier.size((MEDIA_WIDTH * scale).dp, (MEDIA_HEIGHT * scale).dp)
                .legacyAutomationId("pool-tutorial-media-${page.media}"),
            iterations = if (page.loops) LottieConstants.IterateForever else 1,
            speed = page.speed,
            contentScale = ContentScale.Inside
        )
    }
}

private val IndicatorStripHeight = 40.dp

private const val MEDIA_WIDTH = 360f

private const val MEDIA_HEIGHT = 640f
