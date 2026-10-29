package br.gov.caixa.loterias.core.presentation.campaign.theme

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaignTheme
import br.gov.caixa.loterias.core.domain.configuration.model.CampaignArea
import br.gov.caixa.loterias.core.presentation.mapper.toDSCampaign
import br.gov.caixa.loterias.core.presentation.campaign.viewmodel.CampaignViewModel

@Composable
fun ApplicationCampaignTheme(model: CampaignViewModel, content: @Composable () -> Unit) {
    val settings by model.state.collectAsStateWithLifecycle()
    val lifecycle by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
    DisposableEffect(model, lifecycle) {
        model.resumed(lifecycle.isAtLeast(Lifecycle.State.RESUMED))
        onDispose { model.resumed(false) }
    }
    CaixaDSCampaignTheme(
        campaign = settings.active(CampaignArea.HOME).toDSCampaign(),
        megaSenaCampaign = settings.active(CampaignArea.MEGA_SENA).toDSCampaign(),
        content = content,
    )
}
