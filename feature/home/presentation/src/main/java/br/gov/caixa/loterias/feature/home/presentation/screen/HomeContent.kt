package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSScaffold as Scaffold
import br.gov.caixa.loterias.core.designsystem.components.cart.isCartSummaryVisible
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign as HomeCampaign
import br.gov.caixa.loterias.feature.home.presentation.components.HomeCampaign
import br.gov.caixa.loterias.feature.home.presentation.components.HomeCarousel
import br.gov.caixa.loterias.feature.home.presentation.components.HomeLoadingState
import br.gov.caixa.loterias.feature.home.presentation.components.HomeQuickAccess
import br.gov.caixa.loterias.feature.home.presentation.components.HomeTopBar
import br.gov.caixa.loterias.feature.home.presentation.components.infiniteCarouselInitialPage
import br.gov.caixa.loterias.feature.home.presentation.components.loopedCarouselIndex
import br.gov.caixa.loterias.feature.home.presentation.mapper.colors
import br.gov.caixa.loterias.feature.home.presentation.mapper.shouldShowPoolButton
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState

data class HomePresentation(
    val quickAccessExpanded: Boolean,
    val expandedResultPage: Int?,
    val octoberRosePanelVisible: Boolean,
    val carouselOffset: Dp,
    val cloverRotation: Float,
    val quickAccessToggleEnabled: Boolean = true,
    val campaignRotation: Float = 0f,
    val footerScrimAlpha: Float = 0f,
)

data class HomeActions(
    val retry: () -> Unit,
    val openMenu: () -> Unit,
    val openNotifications: () -> Unit,
    val toggleCampaign: () -> Unit,
    val toggleQuickAccess: () -> Unit,
    val readTicket: () -> Unit,
    val responsibleGaming: () -> Unit,
    val socialTransfers: () -> Unit,
    val womenSupport: () -> Unit,
    val toggleResult: (Int) -> Unit,
    val pool: (HomeLottery) -> Unit,
    val bet: (HomeLottery) -> Unit,
    val openCart: () -> Unit,
)

@Composable
fun HomeContent(
    homeState: HomeUiState,
    homeCampaign: HomeCampaign?,
    homeLotteries: List<HomeLottery>,
    resultsState: ResultsUiState,
    requestedModalityId: Int?,
    presentation: HomePresentation,
    actions: HomeActions,
    onQuickAccessContentHeightChanged: (Dp) -> Unit = {},
    onCampaignContentHeightChanged: (Dp) -> Unit = {},
    onHeaderActionsRevealed: () -> Unit = {},
) {
    val contentLoaded = !homeState.loading && !homeState.parametersUnavailable && homeLotteries.isNotEmpty()
    var firstContentFrameDrawn by remember { mutableStateOf(false) }
    val actionsVisible = homeState.headerActionsRevealed || firstContentFrameDrawn
    LaunchedEffect(contentLoaded) {
        if (contentLoaded && !actionsVisible) {
            withFrameNanos { }
            firstContentFrameDrawn = true
            onHeaderActionsRevealed()
        }
    }
    Scaffold(
        containerColor = CaixaDSColorTokens.White,
        topBar = {
            HomeTopBar(
                homeState.unreadNotificationCount,
                homeCampaign,
                presentation.quickAccessExpanded,
                presentation.cloverRotation,
                actions,
                presentation.quickAccessToggleEnabled,
                presentation.campaignRotation,
                actionsVisible = actionsVisible
            )
        },
        modifier = Modifier.uiId("home.0112")
    ) { padding ->
        BoxWithConstraints(
            Modifier.fillMaxSize().padding(padding).background(
                Brush.verticalGradient(
                    listOf(CaixaDSColorTokens.White, CaixaDSColorTokens.HomeGradientEnd)
                )
            )
        ) {
            if (homeLotteries.isEmpty()) {
                HomeLoadingState(homeState.loading, homeState.errorMessage, actions.retry)
                return@BoxWithConstraints
            }
            key(homeLotteries.size) {
                val pagerState = rememberPagerState(
                    initialPage = infiniteCarouselInitialPage(homeLotteries.size),
                    pageCount = { Int.MAX_VALUE }
                )
                val selectedLottery = homeLotteries[loopedCarouselIndex(pagerState.currentPage, homeLotteries.size)]
                LaunchedEffect(requestedModalityId) {
                    val requestedIndex = homeLotteries.indexOfFirst { it.modalityId == requestedModalityId }
                    if (requestedIndex >= 0) {
                        val currentIndex = loopedCarouselIndex(pagerState.currentPage, homeLotteries.size)
                        pagerState.scrollToPage(pagerState.currentPage + requestedIndex - currentIndex)
                    }
                }
                val hasCart = isCartSummaryVisible(homeState.cartTotal)
                val showPoolButton = shouldShowPoolButton(
                    poolSalesEnabled = homeState.features.poolSalesEnabled,
                    parameters = homeState.parameters,
                    lottery = selectedLottery
                )
                val carouselWidth = 280.dp
                val carouselHorizontalPadding = ((maxWidth - carouselWidth) / 2).coerceAtLeast(0.dp)
                HomeQuickAccess(presentation.quickAccessExpanded, homeState, actions, onQuickAccessContentHeightChanged)
                HomeCampaign(presentation.octoberRosePanelVisible, onCampaignContentHeightChanged)
                HomeCarousel(
                    homeState, homeLotteries, resultsState, selectedLottery, pagerState,
                    presentation, carouselWidth, carouselHorizontalPadding, showPoolButton, hasCart, actions
                )
            }
        }
    }
}
