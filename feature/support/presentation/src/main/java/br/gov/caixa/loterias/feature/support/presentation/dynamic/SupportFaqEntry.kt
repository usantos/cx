package br.gov.caixa.loterias.feature.support.presentation.dynamic

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.presentation.delivery.FeatureUiEntry
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

/** Contrato de apresentação da base para o módulo FAQ instalado. */
interface SupportFaqEntry : FeatureUiEntry {
    @Composable
    fun Content(state: SupportUiState, onAction: (SupportAction) -> Unit)
}
