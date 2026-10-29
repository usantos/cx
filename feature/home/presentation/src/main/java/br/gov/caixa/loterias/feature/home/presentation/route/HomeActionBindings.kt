package br.gov.caixa.loterias.feature.home.presentation.route

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import br.gov.caixa.loterias.feature.home.presentation.components.loopedCarouselIndex
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

internal fun homeLegacyActions(
    viewModel: HomeViewModel,
    homeLotteries: List<br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery>,
    quickAccessExpanded: androidx.compose.runtime.MutableState<Boolean>,
    octoberRoseExpanded: androidx.compose.runtime.MutableState<Boolean>,
    drawerState: androidx.compose.material3.DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    onOpen: (String) -> Unit,
    onProtectedAction: ((String) -> Unit)?,
    openWomenSupport: () -> Unit,
): HomeActions = HomeActions(
    retry = viewModel::load,
    openMenu = {
        scope.launch { drawerState.open() }
    },
    openNotifications = {
        onOpen("notifications/messages")
    },
    toggleCampaign = {
        viewModel.resultActions.close()
        scope.launch { switchHomePanel(quickAccessExpanded, octoberRoseExpanded, openCampaign = true) }
    },
    toggleQuickAccess = {
        viewModel.resultActions.close()
        if (!quickAccessExpanded.value) viewModel.loadSocialTransfers()
        scope.launch { switchHomePanel(quickAccessExpanded, octoberRoseExpanded, openCampaign = false) }
    },
    readTicket = {
        quickAccessExpanded.value = false
        onOpen("tickets/read")
    },
    responsibleGaming = {
        quickAccessExpanded.value = false
        onOpen("responsible/start")
    },
    socialTransfers = {
        quickAccessExpanded.value = false
        onOpen("responsible/social")
    },
    womenSupport = {
        quickAccessExpanded.value = false
        openWomenSupport()
    },
    toggleResult = { page ->
        quickAccessExpanded.value = false
        homeLotteries.getOrNull(loopedCarouselIndex(page, homeLotteries.size))
            ?.let { viewModel.resultActions.toggle(page, it.resultCode) }
    },
    pool = { selectedLottery ->
        if (onProtectedAction != null) {
            onProtectedAction("home/pool/${selectedLottery.modalityId}/${selectedLottery.contestType}")
        } else {
            viewModel.startPools(selectedLottery.modalityId, selectedLottery.contestType)
        }
    },
    bet = { selectedLottery ->
        when (selectedLottery.kind) {
            HomeLotteryKind.LOTTERY -> viewModel.startBet(
                selectedLottery.modalityId,
                selectedLottery.contestType,
                selectedLottery.contestNumber
            )

            HomeLotteryKind.INSTANT -> viewModel.startOtherBetting(
                br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction.INSTANT
            )

            HomeLotteryKind.COMBO -> if (onProtectedAction !=
                null
            ) {
                onProtectedAction("home/combo")
            } else {
                viewModel.startOtherBetting(br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingAction.COMBO)
            }
        }
    },
    openCart = {
        if (!quickAccessExpanded.value) onOpen("cart/current")
    }
)

/** PrincipalActivity aguarda toda a sequência de fechamento do outro painel antes de abrir este. */
internal suspend fun switchHomePanel(
    quick: androidx.compose.runtime.MutableState<Boolean>,
    campaign: androidx.compose.runtime.MutableState<Boolean>,
    openCampaign: Boolean,
) {
    if (openCampaign) {
        if (quick.value) {
            quick.value = false
            kotlinx.coroutines.delay(2_500)
        }
        campaign.value = !campaign.value
    } else {
        if (campaign.value) {
            campaign.value = false
            kotlinx.coroutines.delay(1_000)
        }
        quick.value = !quick.value
    }
}
