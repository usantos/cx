package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery

@Composable
fun HomeV2Presentation.HeroCarousel(cards: List<HomeLottery>) {
    val heroCards = cards.take(4)
    if (heroCards.isNotEmpty()) {
        val pager = rememberPagerState(pageCount = { heroCards.size })
        Box(Modifier.padding(top = 12.dp)) {
            HorizontalPager(
                pager,
                contentPadding = PaddingValues(horizontal = 12.dp),
                pageSpacing = 10.dp
            ) { Hero(heroCards[it]) }
        }
        Dots(heroCards.size, pager.currentPage)
    }
}

@Composable private fun HomeV2Presentation.Dots(count: Int, selected: Int) {
    Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), horizontalArrangement = Arrangement.Center) {
        repeat(count) {
            Box(
                Modifier.padding(horizontal = 4.dp).size(if (it == selected) 12.dp else 6.dp, 6.dp)
                    .background(if (it == selected) blue else CxHomeV2Colors.CFFCAD5E7, CircleShape)
            )
        }
    }
}
