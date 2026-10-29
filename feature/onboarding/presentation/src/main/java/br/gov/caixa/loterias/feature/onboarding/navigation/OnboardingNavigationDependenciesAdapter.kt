package br.gov.caixa.loterias.feature.onboarding.navigation

import br.gov.caixa.loterias.feature.onboarding.domain.usecase.CompleteIntroductionUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.SetBettingTutorialDismissedUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.usecase.ShouldShowIntroductionUseCase
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetMarketplaceFilterParametersUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.IsVersionSupportedUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.PrepareNetworkSecurityUseCase
import javax.inject.Inject

class OnboardingNavigationDependenciesAdapter @Inject constructor(
    override val completeIntroduction: CompleteIntroductionUseCase,
    override val setBettingTutorialDismissed: SetBettingTutorialDismissedUseCase,
    override val shouldShowIntroduction: ShouldShowIntroductionUseCase,
    private val versionName: br.gov.caixa.loterias.core.navigation.configuration.HostFeatureConfiguration,
    private val isVersionSupported: IsVersionSupportedUseCase,
    private val marketplaceFilterParameters: GetMarketplaceFilterParametersUseCase,
    private val networkSecurity: PrepareNetworkSecurityUseCase,
    private val networkAvailability: NetworkAvailability,
    private val dispatchers: CoroutineDispatchers,
    private val statusPresentation: StartupStatusPresentation,
) : OnboardingNavigationDependencies {
    @Composable
    override fun StartupStatusDestination(route: String, onRecovered: () -> Unit) =
        OnboardingStartupStatusDestination(
            route = route,
            onRecovered = onRecovered,
            versionName = versionName.appVersionName,
            isVersionSupported = isVersionSupported,
            marketplaceFilterParameters = marketplaceFilterParameters,
            networkSecurity = networkSecurity,
            networkAvailability = networkAvailability,
            dispatchers = dispatchers,
            statusPresentation = statusPresentation,
        )
}
