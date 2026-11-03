package br.gov.caixa.loterias.feature.betting.presentation.delivery

import androidx.compose.runtime.Composable
import coil3.ImageLoader

interface BettingShakeTutorialScreenEntry {
    @Composable
    fun ShakeTutorialScreen(
        page: Int,
        loader: ImageLoader,
        onPrevious: () -> Unit,
        onNext: () -> Unit,
        onPage: (Int) -> Unit,
        onComplete: () -> Unit,
    )
}
