package br.gov.caixa.loterias.apostas.theme.campaign

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import br.gov.caixa.loterias.core.domain.configuration.usecase.CampaignConfigurationUseCasesImpl
import br.gov.caixa.loterias.core.presentation.campaign.viewmodel.CampaignViewModel

@Composable
internal fun ApplicationCampaignTheme(dependencies: AppCampaignDependencies, content: @Composable () -> Unit) {
    val model = viewModel {
        CampaignViewModel(CampaignConfigurationUseCasesImpl(
            dependencies.configuration,
            dependencies.networkSecurity,
            dependencies.homeFeatureConfiguration,
        ), dependencies.dispatchers)
    }
    br.gov.caixa.loterias.core.presentation.campaign.theme.ApplicationCampaignTheme(model, content)
}
