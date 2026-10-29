package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDS
import br.gov.caixa.loterias.feature.betting.R as LocalizedR
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun RapidPageIndicator(page: Int, onPage: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(40.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(RAPID_PAGE_COUNT) { index ->
            val stepDescription = stringResource(LocalizedR.string.format_step_description, index + 1, RAPID_PAGE_COUNT)
            Box(
                Modifier.width(15.dp).fillMaxHeight()
                    .semantics {
                        contentDescription = stepDescription
                        selected = index == page
                    }
                    .uiId("bet.0155").clickable { onPage(index) },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier.size(5.dp)
                        .background(if (index == page) CaixaDS.colors.teal else CaixaDS.colors.surface, CircleShape)
                        .border(1.dp, CaixaDS.colors.teal, CircleShape)
                )
            }
        }
    }
}

/** Observa apenas deslizes concluídos; páginas intermediárias da animação nunca devem substituir o ponto de destino solicitado. */
@Composable
internal fun RapidPagerSynchronization(pager: PagerState, requestedPage: Int, onSettled: (Int) -> Unit) {
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

internal const val RAPID_PAGE_COUNT = 7
