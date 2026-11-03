package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaignTheme
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSHomeV2Theme
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

@Composable
fun RenderHomeV2Content(
    lotteries: List<HomeLottery>,
    loading: Boolean,
    results: Map<String, DrawResult>,
    cardOrder: List<String>,
    onMoveModality: (String, String) -> Unit,
    actions: HomeV2Presentation.Actions,
    campaign: LoteriasCaixaDSCampaign = LocalLoteriasCaixaDSCampaign.current,
    megaSenaCampaign: LoteriasCaixaDSCampaign = LocalLoteriasCaixaDSMegaSenaCampaign.current,
) {
    val currentActions by rememberUpdatedState(actions)
    val currentOrder by rememberUpdatedState(cardOrder)
    val currentMove by rememberUpdatedState(onMoveModality)
    val presentation = remember { HomeV2Presentation({ currentOrder }, { from, to -> currentMove(from, to) }) { currentActions } }
    LoteriasCaixaDSHomeV2Theme {
        LoteriasCaixaDSCampaignTheme(campaign, megaSenaCampaign) { presentation.HomeComposition(lotteries, loading, results) }
    }
}
