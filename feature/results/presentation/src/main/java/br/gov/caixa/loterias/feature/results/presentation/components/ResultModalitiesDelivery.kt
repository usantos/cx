package br.gov.caixa.loterias.feature.results.presentation.components

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.results.presentation.delivery.ResultsUiEntry

@Composable
internal fun ResultModalityScreen(onBack: () -> Unit, onSelectModality: (String) -> Unit) {
    FeatureModuleContent(
        module = AppModule.RESULTS,
        entryType = ResultsUiEntry::class.java,
        onBack = onBack
    ) { entry ->
        entry.ResultModalityScreen(onBack, onSelectModality)
    }
}
