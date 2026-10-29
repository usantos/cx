package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.runtime.*
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaignTheme
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSHomeV2Theme
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery

@Composable
fun HomeV2Content(
    lotteries: List<HomeLottery>, loading: Boolean, results: Map<String, DrawResult>,
    cardOrder: List<String>, onMoveModality: (String, String) -> Unit, actions: HomeV2Presentation.Actions,
    campaign: CaixaDSCampaign = LocalCaixaDSCampaign.current,
    megaSenaCampaign: CaixaDSCampaign = LocalCaixaDSMegaSenaCampaign.current,
) {
    val currentActions by rememberUpdatedState(actions)
    val currentOrder by rememberUpdatedState(cardOrder)
    val currentMove by rememberUpdatedState(onMoveModality)
    val presentation = remember { HomeV2Presentation({ currentOrder }, { from, to -> currentMove(from, to) }) { currentActions } }
    CaixaDSHomeV2Theme {
        CaixaDSCampaignTheme(campaign, megaSenaCampaign) { presentation.Home(lotteries, loading, results) }
    }
}
