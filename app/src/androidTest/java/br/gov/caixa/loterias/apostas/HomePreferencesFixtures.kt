package br.gov.caixa.loterias.apostas

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LocalCaixaDSMegaSenaCampaign
import br.gov.caixa.loterias.feature.betting.domain.repository.ShakeSettingsRepository
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import br.gov.caixa.loterias.feature.home.domain.usecase.HomePreferencesUseCases
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Content
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import kotlinx.coroutines.Dispatchers

/** Os dados de teste em memória executam imediatamente; o armazenamento de produção usa os dispatchers IO injetados. */
internal fun homePreferencesFixture(layout: HomeLayoutRepository, shake: ShakeSettingsRepository = EmptyShakeSettings) =
    HomePreferencesViewModel(HomePreferencesUseCases(layout, shake), FixedCoroutineDispatchers(Dispatchers.Main.immediate))

@Composable
internal fun HomeV2TestContent(
    lotteries: List<HomeLottery>, loading: Boolean, results: Map<String, DrawResult>,
    preferences: HomeLayoutRepository, actions: HomeV2Presentation.Actions,
    campaign: CaixaDSCampaign = LocalCaixaDSCampaign.current,
    megaSenaCampaign: CaixaDSCampaign = LocalCaixaDSMegaSenaCampaign.current,
) {
    val vm = remember(preferences) { homePreferencesFixture(preferences) }
    DisposableEffect(vm) {
        val store = ViewModelStore().apply { put("fixture", vm) }
        onDispose { store.clear() }
    }
    val state by vm.state.collectAsState()
    HomeV2Content(lotteries, loading, results, state.cardOrder, { from, to -> vm.moveModality(from, to) }, actions, campaign, megaSenaCampaign)
}

private object EmptyShakeSettings : ShakeSettingsRepository {
    override suspend fun enabled() = false
    override suspend fun setEnabled(enabled: Boolean) = Unit
    override suspend fun warningHidden() = false
    override suspend fun setWarningHidden(hidden: Boolean) = Unit
    override suspend fun tutorialCompleted(ownerKey: String?) = true
    override suspend fun completeTutorial(ownerKey: String?) = Unit
    override suspend fun tooltipPending(ownerKey: String?) = false
    override suspend fun consumeTooltip(ownerKey: String?) = Unit
}
