package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
internal fun MarketplaceCarousel(
    state: MarketplaceUiState,
    onDecrease: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onFavorite: (String) -> Unit,
    onDetail: (String) -> Unit,
    onAdd: (String) -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val pools = state.carouselPools
    val pager = rememberPagerState { pools.size }
    val coroutine = rememberCoroutineScope()
    Column(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 10.dp)) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            HorizontalPager(
                pager,
                modifier = Modifier.fillMaxWidth().height(461.dp).legacyAutomationId("marketplace-carousel"),
                pageSize = PageSize.Fixed(280.dp),
                contentPadding = PaddingValues(horizontal = ((maxWidth - 280.dp) / 2).coerceAtLeast(0.dp))
            ) { page ->
                val pool = pools[page]
                val offset = abs((pager.currentPage - page) + pager.currentPageOffsetFraction).coerceIn(0f, 1f)
                Box(
                    Modifier.graphicsLayer {
                        scaleX = 1f - .2f * offset
                        scaleY = scaleX
                    }
                ) {
                    MarketplaceCarouselCard(
                        pool,
                        state.selectedShares[pool.code] ?: 1,
                        state.lotteryFiltered,
                        pool.lotteryId in state.updatingFavoriteLotteryIds,
                        pool.code in state.addingPoolCodes,
                        MarketplaceCardActions(
                            { onDecrease(pool.code) },
                            { onIncrease(pool.code) },
                            { onFavorite(pool.code) },
                            { onDetail(pool.code) },
                            { onAdd(pool.code) }
                        ),
                        state.modalityNames,
                        state.specialContests
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.Center) {
            pools.forEachIndexed { index, _ ->
                Box(
                    Modifier.padding(horizontal = 3.dp).size(12.dp).clip(CircleShape)
                        .background(
                            if (pager.currentPage ==
                                index
                            ) {
                                LoteriasCaixaDSColorTokens.CaixaBlue
                            } else {
                                LoteriasCaixaDSColorTokens.White.copy(alpha = 0f)
                            }
                        )
                        .border(1.dp, LoteriasCaixaDSColorTokens.CaixaBlue, CircleShape)
                        .uiId("pool.0009").clickable(role = Role.Button) { coroutine.launch { pager.animateScrollToPage(index) } }
                        .semantics {
                            selected = pager.currentPage == index
                            contentDescription =
                                uiStrings.getString(
                                    LocalizedR.string.ui_bolao_1_s_de_2_s_214183,
                                    (index + 1).toString(),
                                    pools.size.toString()
                                )
                        }
                )
            }
        }
    }
}
