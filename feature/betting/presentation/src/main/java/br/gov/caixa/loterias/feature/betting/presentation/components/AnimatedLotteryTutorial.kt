package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingFlowUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.TutorialDefinition

@Composable
internal fun AnimatedLotteryTutorial(
    state: BettingFlowUiState,
    definition: TutorialDefinition,
    onPageSelected: (Int) -> Unit,
    onClose: () -> Unit,
    onPrimary: () -> Unit,
    onSecondary: () -> Unit,
    onToggleDoNotShowAgain: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = state.page, pageCount = definition.pages::size)
    val density = LocalDensity.current
    val enlargedFont = density.fontScale > 1f
    var navigationHeight by remember { mutableIntStateOf(0) }
    val navigationInset = if (enlargedFont) with(density) { navigationHeight.toDp() } + IndicatorStripHeight else 0.dp
    val actionColor = if (state.spec.route == "betting/tutorial") {
        LoteriasCaixaDSColorTokens.PoolTutorialAction
    } else {
        LoteriasCaixaDSColorTokens.TutorialGreen
    }

    RapidPagerSynchronization(pagerState, state.page, onPageSelected)
    if (state.spec.route == "betting/tutorial") {
        PoolTutorialLayout(
            state,
            definition,
            pagerState,
            onClose,
            onPrimary,
            onSecondary,
            onToggleDoNotShowAgain,
            onPageSelected
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(LoteriasCaixaDS.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize().padding(bottom = navigationInset)) { index ->
            LottieTutorialPage(definition.pages[index], index, definition.pages.size, index == pagerState.currentPage)
        }

        AnimatedTutorialTopActions(state, definition, onClose, onSecondary)
        AnimatedTutorialPrimaryAction(state, definition, actionColor, onPrimary, onToggleDoNotShowAgain)
        if (enlargedFont) {
            AnimatedTutorialNavigation(
                state.page,
                definition.pages.lastIndex,
                onPageSelected,
                Modifier.align(Alignment.BottomCenter).padding(bottom = IndicatorStripHeight).onSizeChanged { navigationHeight = it.height }
            )
        }

        TutorialIndicator(
            pageCount = definition.pages.size,
            selectedPage = state.page,
            selectedColor = LoteriasCaixaDSColorTokens.TutorialIndicator,
            unselectedColor = LoteriasCaixaDSColorTokens.TutorialIndicatorOutline,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = ContentInset)
        )
    }
}

private val ContentInset = 16.dp
private val IndicatorStripHeight = 40.dp
