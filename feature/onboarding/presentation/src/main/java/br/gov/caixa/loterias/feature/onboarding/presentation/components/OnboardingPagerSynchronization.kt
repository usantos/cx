package br.gov.caixa.loterias.feature.onboarding.presentation.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import br.gov.caixa.loterias.feature.onboarding.presentation.viewmodel.OnboardingViewModel

@Composable
internal fun SynchronizeIntroductionPager(pagerState: PagerState, viewModel: OnboardingViewModel) {
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect(viewModel::pageChanged)
    }
}
