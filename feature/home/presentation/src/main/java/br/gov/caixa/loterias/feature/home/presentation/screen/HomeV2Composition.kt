package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSOctoberRoseCampaignPanel
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.R
import br.gov.caixa.loterias.feature.home.R as LocalizedR
import br.gov.caixa.loterias.feature.home.presentation.components.BottomNavigation
import br.gov.caixa.loterias.feature.home.presentation.components.Header
import br.gov.caixa.loterias.feature.home.presentation.components.HeroCarousel
import br.gov.caixa.loterias.feature.home.presentation.components.PoolBanner
import br.gov.caixa.loterias.feature.home.presentation.components.ReorderableModalities
import br.gov.caixa.loterias.feature.home.presentation.components.ResultCard
import br.gov.caixa.loterias.feature.home.presentation.components.Shortcut
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.mapper.tipoModalidade
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import kotlinx.coroutines.launch

@Composable
internal fun HomeV2Presentation.HomeComposition(modalidades: List<HomeLottery>, loading: Boolean, results: Map<String, DrawResult>) {
    val scroll = rememberScrollState()
    val generalCampaign = LocalCaixaDSCampaign.current
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
            if (campaignInfoExpanded && generalCampaign == CaixaDSCampaign.OCTOBER_ROSE) {
                CaixaDSOctoberRoseCampaignPanel()
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
        Column(Modifier.padding(top = if (poolCard != null) 8.dp else 0.dp)) {
            Section(
                uiStrings.getString(LocalizedR.string.ui_escolha_sua_modalidade_e5181a),
                if (showAll) {
                    uiStrings.getString(
                        LocalizedR.string.ui_ver_menos_8e3816
                    )
                } else {
                    uiStrings.getString(LocalizedR.string.ui_ver_todas_6240cd)
                }
            ) { onShowAll(!showAll) }
            Text(
                uiStrings.getString(LocalizedR.string.ui_segure_e_arraste_para_organizar_ffb77b),
                fontFamily = regular,
                fontSize = 11.sp,
                color = CxHomeV2Colors.CFF647C94,
                modifier = (Modifier.padding(top = 3.dp)).uiId("home.0147")
            )
        }
        val arrangedCards = cards.sortedBy { cardOrder.indexOf(it.tipoModalidade.name) }
        ReorderableModalities(if (showAll) arrangedCards else arrangedCards.take(8), scroll, viewport) { onShowAll(true) }
        Spacer(Modifier.height(5.dp))
        Section(uiStrings.getString(LocalizedR.string.ui_ultimos_resultados_efecc0), action = actions::results)
        Row(
            Modifier.fillMaxWidth().height(IntrinsicSize.Min).uiId("home.0148").horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cards.filter {
                it.tipoModalidade in listOf(
                    HomeV2Modality.MEGA_SENA,
                    HomeV2Modality.MAIS_MILIONARIA,
                    HomeV2Modality.QUINA
                )
            }.forEach { ResultCard(it, results[it.resultCode]) }
        }
        Spacer(Modifier.height(4.dp))
        Title(uiStrings.getString(LocalizedR.string.ui_mais_para_voce_eabc1d), 18)
        ShortcutActions()
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun HomeV2Presentation.ShortcutActions() {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Shortcut(
            uiStrings.getString(LocalizedR.string.ui_meus_jogos_08fc53),
            uiStrings.getString(LocalizedR.string.ui_acompanhe_suas_apostas_7c205a),
            R.drawable.ic_home_v2_bilhete,
            (Modifier.weight(1f)).uiId("home.0149"),
            actions::games
        )
        Shortcut(
            "Resultados",
            uiStrings.getString(LocalizedR.string.ui_consulte_historicos_661e76),
            R.drawable.ic_home_v2_resultados,
            (Modifier.weight(1f)).uiId("home.0150"),
            actions::results
        )
        Shortcut(
            uiStrings.getString(LocalizedR.string.ui_estatisticas_003851),
            uiStrings.getString(LocalizedR.string.ui_veja_numeros_e_probabilidades_04757e),
            R.drawable.ic_home_v2_estatisticas,
            (Modifier.weight(1f)).uiId("home.0151"),
            actions::results
        )
        Shortcut(
            uiStrings.getString(LocalizedR.string.ui_promocoes_2a838e),
            uiStrings.getString(LocalizedR.string.ui_fique_por_dentro_das_novidades_bdfd42),
            R.drawable.ic_home_v2_presente,
            (Modifier.weight(1f)).uiId("home.0152"),
            actions::notifications
        )
    }
}

@Composable private fun HomeV2Presentation.Section(
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

@Composable private fun HomeV2Presentation.Title(text: String, size: Int) {
    Text(text, color = navy, fontFamily = bold, fontSize = size.sp, modifier = Modifier.uiId("home.0155"))
}

private fun kotlinx.coroutines.CoroutineScope.launchScroll(state: ScrollState, target: Int) {
    launch { state.animateScrollTo(target.coerceAtMost(state.maxValue)) }
}
