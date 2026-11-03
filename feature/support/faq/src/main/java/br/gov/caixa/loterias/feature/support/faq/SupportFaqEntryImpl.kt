package br.gov.caixa.loterias.feature.support.faq

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.support.faq.screen.FaqContent
import br.gov.caixa.loterias.feature.support.presentation.dynamic.SupportFaqEntry
import br.gov.caixa.loterias.feature.support.presentation.screen.SupportContentError
import br.gov.caixa.loterias.feature.support.presentation.state.SupportAction
import br.gov.caixa.loterias.feature.support.presentation.state.SupportUiState

/** Resolvida pela base apenas após confirmar a instalação do módulo. */
@Keep
class SupportFaqEntryImpl @Keep constructor() : SupportFaqEntry {
    @Composable
    override fun Content(state: SupportUiState, onAction: (SupportAction) -> Unit) {
        val errorMessage = state.errorMessage
        if (errorMessage != null) {
            SupportContentError(errorMessage)
        } else {
            FaqContent(state, onAction)
        }
    }
}
