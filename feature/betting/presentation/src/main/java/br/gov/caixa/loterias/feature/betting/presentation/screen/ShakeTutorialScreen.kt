package br.gov.caixa.loterias.feature.betting.presentation.screen

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.core.domain.configuration.model.AppModule
import br.gov.caixa.loterias.core.presentation.delivery.FeatureModuleContent
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingUiEntry
import coil3.ImageLoader

@Composable
fun ShakeTutorialScreen(
    page: Int,
    loader: ImageLoader,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onPage: (Int) -> Unit,
    onComplete: () -> Unit,
) {
    FeatureModuleContent(
        module = AppModule.BETTING,
        entryType = BettingUiEntry::class.java,
        onBack = onComplete,
        preserveActive = false
    ) { entry ->
        entry.ShakeTutorialScreen(page, loader, onPrevious, onNext, onPage, onComplete)
    }
}
