package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions
import br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import kotlin.math.absoluteValue

@Composable
internal fun HomeCarousel(
    homeState: HomeUiState,
    homeLotteries: List<HomeLottery>,
    resultsState: ResultsUiState,
    selectedLottery: HomeLottery,
    pagerState: PagerState,
    presentation: HomePresentation,
    carouselWidth: Dp,
    carouselHorizontalPadding: Dp,
    showPoolButton: Boolean,
    hasCart: Boolean,
    actions: HomeActions,
) {
    if (LocalDensity.current.fontScale > 1f) {
        HomeLargeFontCarousel(
            homeState, homeLotteries, resultsState, selectedLottery, pagerState, presentation,
            carouselWidth, carouselHorizontalPadding, showPoolButton, hasCart, actions
        )
        return
    }
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val quickAccessExpanded = presentation.quickAccessExpanded
    val expandedResultPage = presentation.expandedResultPage
    val octoberRosePanelVisible = presentation.octoberRosePanelVisible
    val carouselOffset = presentation.carouselOffset
    val quickAccessMoving = quickAccessExpanded || octoberRosePanelVisible || carouselOffset > 0.dp
    Box(Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                snapAnimationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
            ),
            userScrollEnabled = !quickAccessMoving && !octoberRosePanelVisible && expandedResultPage == null,
            modifier = Modifier.fillMaxWidth().padding(bottom = 116.dp)
                .offset(y = carouselOffset),
            pageSize = PageSize.Fixed(carouselWidth),
            contentPadding = PaddingValues(horizontal = carouselHorizontalPadding),
            pageSpacing = 0.dp
        ) { page ->
            HomeLegacyPagerCard(
                lottery = homeLotteries[loopedCarouselIndex(page, homeLotteries.size)],
                resultState = resultsState,
                resultExpanded = expandedResultPage == page,
                onToggleResult = { actions.toggleResult(page) },
                distance = {
                    ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                        .absoluteValue.coerceIn(0f, 1f)
                }
            )
        }
        if (!octoberRosePanelVisible && presentation.footerScrimAlpha > 0f) {
            val minimumLine = androidx.compose.ui.text.rememberTextMeasurer().measure(
                uiStrings.getString(LocalizedR.string.ui_valor_minimo_da_compra_r_0_00_b65ede),
                style = homeLegacyTextStyle(10.sp)
            ).size.height
            val scrimHeight = with(LocalDensity.current) { minimumLine.toDp() } + 46.dp
            Box(
                Modifier.fillMaxWidth().height(scrimHeight).align(Alignment.BottomCenter)
                    .background(CaixaDSColorTokens.White.copy(alpha = presentation.footerScrimAlpha))
            )
        }
        if (expandedResultPage == null) {
            if (!quickAccessMoving) {
                HomeLegacyCardActions(homeState, selectedLottery, showPoolButton, quickAccessExpanded, actions)
            }
            HomeCartSummary(homeState, hasCart, actions)
        }
    }
}
