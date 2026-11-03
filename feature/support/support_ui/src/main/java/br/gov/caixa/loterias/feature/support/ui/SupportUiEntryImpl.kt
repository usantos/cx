package br.gov.caixa.loterias.feature.support.ui

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.feature.support.presentation.delivery.SupportUiEntry
import br.gov.caixa.loterias.feature.support.presentation.screen.RenderGameHelpScreen
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

@Keep
class SupportUiEntryImpl : SupportUiEntry {
    @Composable
    override fun GameHelp(state: SupportUiState, onBack: () -> Unit, modifier: Modifier) {
        RenderGameHelpScreen(state, onBack, modifier)
    }
}
