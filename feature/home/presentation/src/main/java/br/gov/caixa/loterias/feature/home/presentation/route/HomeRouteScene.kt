package br.gov.caixa.loterias.feature.home.presentation.route

import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign as HomeCampaign
import br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel

internal data class HomeRouteContext(
    val viewModel: HomeViewModel,
    val preferencesViewModel: br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel?,
    val userName: String?,
    val environmentLabel: String?,
    val onOpen: (String) -> Unit,
    val onProtectedAction: ((String) -> Unit)?,
    val onDrawerOpen: (String) -> Unit,
    val onLogout: () -> Unit,
    val requestedModalityId: Int?,
)

internal data class HomeRoutePanels(
    val quick: androidx.compose.runtime.MutableState<Boolean>,
    val campaign: androidx.compose.runtime.MutableState<Boolean>,
    val presentation: HomePresentation,
    val onQuickHeight: (androidx.compose.ui.unit.Dp) -> Unit,
    val onCampaignHeight: (androidx.compose.ui.unit.Dp) -> Unit,
)

internal data class HomeRouteScene(
    val homeState: br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState,
    val preferences: br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState?,
    val lotteries: List<br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery>,
    val campaign: HomeCampaign?,
    val lifecycle: androidx.lifecycle.Lifecycle.State,
    val panels: HomeRoutePanels,
    val navigationBarInset: Float,
    val density: androidx.compose.ui.unit.Density,
    val classic: Boolean,
)
