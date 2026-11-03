package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/** Observa apenas deslizes concluídos; páginas intermediárias da animação nunca devem substituir o ponto de destino solicitado. */
@Composable
fun RapidPagerSynchronization(pager: PagerState, requestedPage: Int, onSettled: (Int) -> Unit) {
    var navigating by remember(pager) { mutableStateOf(false) }
    val latestOnSettled by rememberUpdatedState(onSettled)
    LaunchedEffect(pager, requestedPage) {
        if (pager.settledPage != requestedPage || pager.currentPageOffsetFraction != 0f) {
            navigating = true
            try {
                pager.animateScrollToPage(requestedPage)
            } finally {
                navigating = false
            }
        }
    }
    LaunchedEffect(pager) {
        snapshotFlow { pager.isScrollInProgress to pager.settledPage }
            .distinctUntilChanged().collect { (scrolling, page) ->
                if (!scrolling && !navigating) latestOnSettled(page)
            }
    }
}

const val RAPID_PAGE_COUNT = 7
