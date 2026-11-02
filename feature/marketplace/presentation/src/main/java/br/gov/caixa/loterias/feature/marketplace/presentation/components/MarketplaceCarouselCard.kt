package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool

@Composable
internal fun MarketplaceCarouselCard(
    pool: MarketplacePool,
    shares: Int,
    lotteryFiltered: Boolean,
    updatingFavorite: Boolean,
    adding: Boolean,
    actions: MarketplaceCardActions,
    modalityNames: Map<Pair<String, Int>, String>,
    specialContests: br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests,
) {
    if (LocalDensity.current.fontScale > 1f) {
        MarketplaceLargeFontCarouselCard(pool, shares, lotteryFiltered, updatingFavorite, adding, actions, modalityNames, specialContests)
        return
    }
    val colors = marketplaceColors(pool.modality)
    Box(Modifier.width(280.dp).height(461.dp)) {
        Box(
            Modifier.fillMaxWidth().height(
                18.dp
            ).clip(
                RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
            ).background(if (lotteryFiltered) colors.light else colors.carouselHeader)
        )
        Box(Modifier.fillMaxWidth().offset(y = 18.dp).height(393.dp).background(LoteriasCaixaDSColorTokens.White))
        MarketplacePoolIdentity(pool, lotteryFiltered, updatingFavorite, actions.favorite, colors, modalityNames, specialContests)
        MarketplacePoolOffer(pool, shares, colors, actions.detail)
        MarketplacePoolQuantity(pool, shares, adding, colors, actions)
        MarketplacePoolAddAction(pool, adding, colors, actions.add)
    }
}

internal data class MarketplaceCardActions(
    val decrease: () -> Unit,
    val increase: () -> Unit,
    val favorite: () -> Unit,
    val detail: () -> Unit,
    val add: () -> Unit,
)
