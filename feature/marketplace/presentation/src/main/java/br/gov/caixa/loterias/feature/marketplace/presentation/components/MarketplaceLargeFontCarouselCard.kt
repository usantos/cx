package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.LoteriasCaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests

@Composable
internal fun MarketplaceLargeFontCarouselCard(
    pool: MarketplacePool,
    shares: Int,
    lotteryFiltered: Boolean,
    updatingFavorite: Boolean,
    adding: Boolean,
    actions: MarketplaceCardActions,
    modalityNames: Map<Pair<String, Int>, String>,
    specialContests: MarketplaceSpecialContests,
) {
    val colors = marketplaceColors(pool.modality)
    Column(
        Modifier.width(280.dp).height(461.dp).background(LoteriasCaixaDSColorTokens.White)
            .uiId("pool.0081").verticalScroll(rememberScrollState())
    ) {
        Box(
            Modifier.fillMaxWidth().height(18.dp).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(if (lotteryFiltered) colors.light else colors.carouselHeader)
        )
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LargeFontPoolIdentity(pool, lotteryFiltered, updatingFavorite, actions.favorite, colors, modalityNames, specialContests)
            LargeFontPoolOffer(pool, shares, colors, actions.detail)
            Text(
                stringResource(R.string.ui_quantidade_de_cotas_ddb3fd),
                (Modifier.fillMaxWidth()).uiId("pool.0085"),
                style = marketplaceText(CAROUSEL_BODY_SIZE),
                color = LoteriasCaixaDSColorTokens.TicketChoiceMuted,
                textAlign = TextAlign.Center
            )
            LargeFontShareQuantity(pool, shares, adding, colors, actions)
            LargeFontPoolAddAction(pool, adding, colors, actions.add)
        }
    }
}

internal const val CAROUSEL_BODY_SIZE = 16
internal const val CAROUSEL_VALUE_SIZE = 18
internal const val CAROUSEL_PRIZE_SIZE = 22
internal const val CAROUSEL_CAPTION_SIZE = 14
