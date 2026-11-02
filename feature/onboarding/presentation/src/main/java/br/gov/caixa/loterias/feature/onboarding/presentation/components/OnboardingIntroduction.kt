package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDS
import br.gov.caixa.loterias.feature.onboarding.R as LocalizedR
import br.gov.caixa.loterias.feature.onboarding.presentation.state.OnboardingUiState

internal data class IntroductionActions(
    val pageChanged: (Int) -> Unit,
    val complete: () -> Unit,
    val confirmAdult: () -> Unit,
    val rejectAdult: () -> Unit,
)

@Composable
internal fun IntroductionScreen(state: OnboardingUiState, actions: IntroductionActions) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val pagerState = rememberPagerState(initialPage = state.introductionPage, pageCount = introPages::size)
    SynchronizeIntroductionPager(pagerState, actions.pageChanged)
    val density = LocalDensity.current
    val largeFont = density.fontScale > 1f
    var footerHeight by remember { mutableIntStateOf(0) }
    Box(
        Modifier.fillMaxSize().background(
            Brush.horizontalGradient(listOf(LoteriasCaixaDS.colors.headerStart, LoteriasCaixaDS.colors.headerEnd))
        )
    ) {
        IntroBackground()
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                .padding(bottom = if (largeFont) with(density) { footerHeight.toDp() } else 0.dp).semantics {
                    contentDescription = uiStrings.getString(
                        LocalizedR.string.ui_pagina_1_s_de_2_s_ecbc1a,
                        (state.introductionPage + 1).toString(),
                        introPages.size.toString()
                    )
                }
        ) { page ->
            LegacyIntroPage(introPages[page], scrollable = largeFont)
        }
        if (largeFont) {
            LargeFontIntroductionFooter(
                pagerState.currentPage,
                state.introductionPage == introPages.lastIndex,
                actions.complete,
                Modifier.align(Alignment.BottomCenter).navigationBarsPadding().onSizeChanged { footerHeight = it.height }
            )
        } else {
            LegacyIntroductionFooter(
                pagerState.currentPage,
                state.introductionPage == introPages.lastIndex,
                actions.complete,
                Modifier.align(Alignment.BottomCenter)
            )
        }
    }
    if (state.ageConfirmationVisible) {
        AgeConfirmationDialog(
            onConfirmAdult = actions.confirmAdult,
            onRejectAdult = actions.rejectAdult
        )
    }
}
