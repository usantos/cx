package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.presentation.delivery.FeatureEntryAccess
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.home.presentation.delivery.HomeDrawerActions
import br.gov.caixa.loterias.feature.home.presentation.delivery.HomeUiEntry

@Composable
fun HomeDrawer(
    features: HomeFeatureConfiguration,
    preferenceState: br.gov.caixa.loterias.feature.home.presentation.state.HomePreferencesUiState? = null,
    onToggleShake: (Boolean?) -> Unit = {},
    onConsumeShakeTooltip: () -> Unit = {},
    drawerOpen: Boolean = true,
    userName: String?,
    environmentLabel: String?,
    campaign: br.gov.caixa.loterias.core.domain.configuration.model.CommemorativeCampaign? = null,
    onOpen: (String) -> Unit,
    onLogout: () -> Unit,
    onClose: () -> Unit,
    onExternalFailure: () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.HOME,
        entryType = HomeUiEntry::class.java,
        onBack = onClose,
        access = FeatureEntryAccess.OPTIONAL,
        preserveActive = false
    ) { entry ->
        entry.HomeDrawer(
            features,
            preferenceState,
            drawerOpen,
            userName,
            environmentLabel,
            campaign,
            HomeDrawerActions(onToggleShake, onConsumeShakeTooltip, onOpen, onLogout, onClose, onExternalFailure)
        )
    }
}
