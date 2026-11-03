package br.gov.caixa.loterias.feature.betting.ui

import androidx.compose.runtime.Composable
import br.gov.caixa.loterias.feature.betting.presentation.delivery.BettingShakeTutorialScreenEntry
import coil3.ImageLoader

internal class BettingShakeTutorialScreenRenderer : BettingShakeTutorialScreenEntry {
    @Composable
    override fun ShakeTutorialScreen(
        page: Int,
        loader: ImageLoader,
        onPrevious: () -> Unit,
        onNext: () -> Unit,
        onPage: (Int) -> Unit,
        onComplete: () -> Unit,
    ) {
        br.gov.caixa.loterias.feature.betting.presentation.screen.ShakeTutorialScreenDelivered(
            page,
            loader,
            onPrevious,
            onNext,
            onPage,
            onComplete
        )
    }
}
