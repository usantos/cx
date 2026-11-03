package br.gov.caixa.loterias.feature.home.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalLoteriasCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.home.presentation.delivery.HomeUiEntry
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

@Composable
fun HomeV2Content(
    lotteries: List<HomeLottery>,
    loading: Boolean,
    results: Map<String, DrawResult>,
    cardOrder: List<String>,
    onMoveModality: (String, String) -> Unit,
    actions: HomeV2Presentation.Actions,
    campaign: LoteriasCaixaDSCampaign = LocalLoteriasCaixaDSCampaign.current,
    megaSenaCampaign: LoteriasCaixaDSCampaign = LocalLoteriasCaixaDSMegaSenaCampaign.current,
) {
    FeatureModuleContent(
        module = AppModule.HOME,
        entryType = HomeUiEntry::class.java,
        onBack = {},
        access = FeatureEntryAccess.OPTIONAL,
        preserveActive = false
    ) { entry ->
        entry.HomeV2Content(lotteries, loading, results, cardOrder, onMoveModality, actions, campaign, megaSenaCampaign)
    }
}
