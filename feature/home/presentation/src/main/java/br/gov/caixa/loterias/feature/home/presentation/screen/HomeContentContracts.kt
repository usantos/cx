package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.ui.unit.Dp
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery

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
