package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.LoteriasCaixaDSOctoberRoseCampaignPanel
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.components.BottomNavigation
import br.gov.caixa.loterias.feature.home.presentation.components.Header
import br.gov.caixa.loterias.feature.home.presentation.components.HeroCarousel
import br.gov.caixa.loterias.feature.home.presentation.components.PoolBanner
import br.gov.caixa.loterias.feature.home.presentation.mapper.tipoModalidade
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import kotlinx.coroutines.launch

@Composable
internal fun HomeV2Presentation.HomeComposition(modalidades: List<HomeLottery>, loading: Boolean, results: Map<String, DrawResult>) {
    val scroll = rememberScrollState()
    val generalCampaign = LocalLoteriasCaixaDSCampaign.current
    var campaignInfoExpanded by androidx.compose.runtime.saveable.rememberSaveable(generalCampaign) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val cards = modalidades.filter { it.tipoModalidade in order }
        .sortedBy { order.indexOf(it.tipoModalidade) }
    var showAll by remember { mutableStateOf(false) }
    var viewport by remember { mutableStateOf(Rect.Zero) }
    var navigationHeight by remember { mutableStateOf(84.dp) }
    val density = LocalDensity.current
    Box(Modifier.fillMaxSize().background(CxHomeV2Colors.CFFF5F7FC)) {
        Column(
            Modifier.fillMaxSize().padding(bottom = navigationHeight).onGloballyPositioned {
                // A rolagem e o arraste compartilham a área visível acima da navegação inferior.
                viewport = it.boundsInRoot()
            }.uiId("home.0145").verticalScroll(scroll)
        ) {
            Header { campaignInfoExpanded = !campaignInfoExpanded }
            if (campaignInfoExpanded && generalCampaign == LoteriasCaixaDSCampaign.OCTOBER_ROSE) {
                LoteriasCaixaDSOctoberRoseCampaignPanel()
            }
            if (loading) {
                HomeShimmer()
            } else {
                HomeSections(cards, results, scroll, viewport, showAll) { showAll = it }
            }
        }
        BottomNavigation(
            (Modifier.align(Alignment.BottomCenter)).uiId("home.0146"),
            density,
            onHeightChanged = { navigationHeight = it }
        ) { scope.launchScroll(scroll, 0) }
    }
}

@Composable
private fun HomeV2Presentation.HomeSections(
    cards: List<HomeLottery>,
    results: Map<String, DrawResult>,
    scroll: ScrollState,
    viewport: Rect,
    showAll: Boolean,
    onShowAll: (Boolean) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    HeroCarousel(cards)
    Column(Modifier.padding(horizontal = 13.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val poolCard = cards.firstOrNull { actions.canPool(it.tipoModalidade) }
        if (poolCard != null) {
            Spacer(Modifier.height(6.dp))
            PoolBanner(poolCard)
        }
        ModalitySection(cards, poolCard != null, scroll, viewport, showAll, onShowAll)
        Spacer(Modifier.height(5.dp))
        LatestResults(cards, results)
        Spacer(Modifier.height(4.dp))
        Title(uiStrings.getString(LocalizedR.string.ui_mais_para_voce_eabc1d), 18)
        ShortcutActions()
        Spacer(Modifier.height(12.dp))
    }
}

@Composable internal fun HomeV2Presentation.Section(
    title: String,
    actionLabel: String = stringResource(LocalizedR.string.format_view_all),
    action: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f)) { Title(title, 18) }
        Text(
            uiStrings.getString(LocalizedR.string.ui_1_s_8d1db6, actionLabel),
            color = blue,
            fontSize = 12.sp,
            modifier = (Modifier.uiId("home.0153").clickable(onClick = action)).uiId("home.0154")
        )
    }
}

@Composable internal fun HomeV2Presentation.Title(text: String, size: Int) {
    Text(text, color = navy, fontFamily = bold, fontSize = size.sp, modifier = Modifier.uiId("home.0155"))
}

private fun kotlinx.coroutines.CoroutineScope.launchScroll(state: ScrollState, target: Int) {
    launch { state.animateScrollTo(target.coerceAtMost(state.maxValue)) }
}
