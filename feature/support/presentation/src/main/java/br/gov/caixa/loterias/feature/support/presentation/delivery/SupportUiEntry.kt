package br.gov.caixa.loterias.feature.support.presentation.delivery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

interface SupportUiEntry : FeatureUiEntry {
    @Composable
    fun GameHelp(state: SupportUiState, onBack: () -> Unit, modifier: Modifier)
}
