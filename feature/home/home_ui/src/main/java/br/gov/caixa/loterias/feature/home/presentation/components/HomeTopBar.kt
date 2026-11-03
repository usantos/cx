package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.components.navigation.LoteriasCaixaDSHomeTopBar
import br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign as HomeCampaign
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions

/** Apenas ligação da funcionalidade; o cabeçalho visual pertence ao LoteriasCaixaDS. */
@Composable
internal fun HomeTopBar(
    unreadNotificationCount: Int,
    homeCampaign: HomeCampaign?,
    quickAccessExpanded: Boolean,
    cloverRotation: Float,
    actions: HomeActions,
    quickAccessToggleEnabled: Boolean = true,
    campaignRotation: Float = 0f,
    actionsVisible: Boolean = true,
): Unit = LoteriasCaixaDSHomeTopBar(
    unreadNotificationCount, homeCampaign == HomeCampaign.OUTUBRO_ROSA,
    quickAccessExpanded, cloverRotation, actions.openMenu, actions.openNotifications,
    actions.toggleCampaign, actions.toggleQuickAccess, quickAccessToggleEnabled, campaignRotation, actionsVisible
)
