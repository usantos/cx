package br.gov.caixa.loterias.feature.retail.presentation.screen

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.retail.presentation.delivery.RetailUiEntry
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateUiState

@Composable
internal fun LocationGateScreen(
    state: LocationGateUiState,
    versionName: String,
    @DrawableRes lotteryIconResourceId: Int,
    onAction: () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.RETAIL,
        entryType = RetailUiEntry::class.java,
        onBack = {}
    ) { entry ->
        entry.LocationGateScreen(state, versionName, lotteryIconResourceId, onAction)
    }
}
