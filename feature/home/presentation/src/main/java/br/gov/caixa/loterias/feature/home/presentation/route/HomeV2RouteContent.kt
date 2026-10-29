package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSPullToRefreshBox as PullToRefreshBox
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign as HomeCampaign
import br.gov.caixa.loterias.core.presentation.mapper.toDSCampaign
import br.gov.caixa.loterias.feature.home.presentation.components.HomeLoadingState
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.mapper.shouldShowPoolButton
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Content
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel

/** Integração condicional V2: os resultados são carregados neste ramo; as configurações chegam do seu ViewModel. */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun HomeV2RouteContent(
    cardOrder: List<String>,
    onMoveModality: (String, String) -> Unit,
    homeState: HomeUiState,
    homeCampaign: HomeCampaign?,
    homeLotteries: List<HomeLottery>,
    density: Density,
    navigationBarInset: Float,
    onOpen: (String) -> Unit,
    onProtectedAction: ((String) -> Unit)?,
    viewModel: HomeViewModel,
    onOpenMenu: () -> Unit,
) {
    val homeResults = homeState.results.cache
    val resultsLoading = homeState.results.loading.isNotEmpty()
    val resultCodes = homeLotteries.map { it.resultCode }.filter {
        it in listOf("megasena", "maismilionaria", "quina")
    }.distinct()
    LaunchedEffect(viewModel, resultCodes, homeState.results.generation) {
        viewModel.resultActions.load(resultCodes)
    }
    PullToRefreshBox(
        isRefreshing = homeLotteries.isNotEmpty() && (homeState.loading || resultsLoading),
        onRefresh = {
            if (!viewModel.state.value.loading && viewModel.state.value.results.loading.isEmpty()) {
                viewModel.load()
                viewModel.refreshCart()
                viewModel.refreshUnreadNotificationCount()
                viewModel.resultActions.load(resultCodes)
            }
        },
        modifier = (
            Modifier.fillMaxSize().statusBarsPadding().padding(
                bottom = with(density) { navigationBarInset.toDp() }
            )
            ).uiId("home.0111")
    ) {
        if (homeLotteries.isEmpty() && !homeState.loading) {
            HomeLoadingState(false, homeState.errorMessage, viewModel::load)
        } else {
            HomeV2Content(
                homeLotteries,
                homeState.loading && homeLotteries.isEmpty(),
                homeResults,
                cardOrder,
                onMoveModality,
                object : HomeV2Presentation.Actions {
                    override fun bet(modalidade: HomeLottery) {
                        if (!homeState.preparingBet) {
                            viewModel.startBet(
                                modalidade.modalityId,
                                modalidade.contestType,
                                modalidade.contestNumber
                            )
                        }
                    }
                    override fun pool(modalidade: HomeLottery) {
                        if (homeState.preparingBet) return
                        if (onProtectedAction != null) {
                            onProtectedAction("home/pool/${modalidade.modalityId}/${modalidade.contestType}")
                        } else {
                            viewModel.startPools(modalidade.modalityId, modalidade.contestType)
                        }
                    }
                    override fun results() = onOpen("results/modalities")
                    override fun modalityResults(modalidade: HomeV2Modality) = onOpen("results/home/${modalidade.resultCode}")
                    override fun games() = onOpen("tickets/my-bets")
                    override fun favorites() = onOpen("betting/favorites")
                    override fun menu() {
                        onOpenMenu()
                    }
                    override fun notifications() = onOpen("notifications/messages")
                    override fun cart() = onOpen("cart/current")
                    override fun profile() = menu()
                    override fun canPool(modalidade: HomeV2Modality): Boolean = homeLotteries.any {
                        it.resultCode == modalidade.resultCode && shouldShowPoolButton(
                            homeState.features.poolSalesEnabled,
                            homeState.parameters,
                            it
                        )
                    }
                },
                campaign = homeCampaign.toDSCampaign(),
                megaSenaCampaign = homeState.features.campaigns.active(CampaignArea.MEGA_SENA).toDSCampaign()
            )
        }
    }
}
