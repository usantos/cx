package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions
import br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import kotlin.math.absoluteValue

/** Corpo, ações e carrinho ocupam áreas medidas separadas; conteúdo ampliado nunca passa por baixo de uma ação. */
@Composable
internal fun HomeLargeFontCarousel(
    homeState: HomeUiState,
    lotteries: List<HomeLottery>,
    results: ResultsUiState,
    selected: HomeLottery,
    pager: PagerState,
    presentation: HomePresentation,
    cardWidth: Dp,
    pagePadding: Dp,
    showPoolButton: Boolean,
    hasCart: Boolean,
    actions: HomeActions,
) {
    val moving = presentation.quickAccessExpanded || presentation.octoberRosePanelVisible || presentation.carouselOffset > 0.dp
    Column(Modifier.fillMaxSize()) {
        HorizontalPager(
            pager,
            Modifier.weight(1f).fillMaxWidth().offset(y = presentation.carouselOffset),
            flingBehavior = PagerDefaults.flingBehavior(
                pager,
                snapAnimationSpec = tween(durationMillis = CAROUSEL_SNAP_MILLIS, easing = FastOutSlowInEasing)
            ),
            userScrollEnabled = !moving && presentation.expandedResultPage == null,
            pageSize = PageSize.Fixed(cardWidth),
            contentPadding = PaddingValues(horizontal = pagePadding),
            pageSpacing = 0.dp
        ) { page ->
            HomeLegacyPagerCard(
                lotteries[loopedCarouselIndex(page, lotteries.size)],
                results,
                presentation.expandedResultPage == page,
                { actions.toggleResult(page) }
            ) {
                ((pager.currentPage - page) + pager.currentPageOffsetFraction).absoluteValue.coerceIn(0f, 1f)
            }
        }
        if (presentation.expandedResultPage == null) {
            if (!moving) HomeLargeFontActions(homeState, selected, showPoolButton, actions)
            if (hasCart) Box(Modifier.fillMaxWidth()) { HomeCartSummary(homeState, true, actions) }
        }
    }
}

@Composable
private fun HomeLargeFontActions(homeState: HomeUiState, lottery: HomeLottery, showPool: Boolean, actions: HomeActions) {
    val enabled = !homeState.preparingBet
    val padding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
        if (showPool) {
            HomePoolAction(
                lottery,
                enabled,
                { actions.pool(lottery) },
                Modifier.fillMaxWidth().padding(vertical = 6.dp).heightIn(min = 56.dp),
                padding
            )
        }
        HomeBetAction(
            lottery,
            enabled && (lottery.open || lottery.kind == HomeLotteryKind.LOTTERY),
            { actions.bet(lottery) },
            Modifier.fillMaxWidth().padding(vertical = 6.dp).heightIn(min = 56.dp),
            padding
        )
    }
}

private const val CAROUSEL_SNAP_MILLIS = 320
