package br.gov.caixa.loterias.feature.support.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.support.presentation.delivery.SupportUiEntry
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

@Composable
internal fun GameHelpScreen(state: SupportUiState, onBack: () -> Unit, modifier: Modifier = Modifier) {
    FeatureModuleContent(
        module = AppModule.SUPPORT_HELP,
        entryType = SupportUiEntry::class.java,
        onBack = onBack
    ) { entry -> entry.GameHelp(state, onBack, modifier) }
}
