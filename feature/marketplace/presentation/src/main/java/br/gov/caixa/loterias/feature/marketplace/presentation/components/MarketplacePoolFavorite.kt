package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.legacyAutomationId
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool

@Composable
internal fun androidx.compose.foundation.layout.BoxScope.MarketplacePoolFavorite(
    pool: MarketplacePool,
    updatingFavorite: Boolean,
    favorite: () -> Unit,
    colors: MarketplaceColors,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(
        Modifier.align(Alignment.TopEnd).padding(end = 4.dp).size(32.dp).clip(CircleShape)
            .background(LoteriasCaixaDSColorTokens.White).border(3.dp, colors.light, CircleShape)
            .legacyAutomationId("carousel-favorite-${pool.code}")
            .uiId("pool.0027").clickable(enabled = pool.lotteryId > 0 && !updatingFavorite, onClick = favorite),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            androidx.compose.ui.res.painterResource(
                if (pool.favoriteLottery) {
                    br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_marketplace_favorite
                } else {
                    br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_marketplace_unfavorite
                }
            ),
            if (pool.favoriteLottery) {
                uiStrings.getString(
                    LocalizedR.string.ui_excluir_loterica_favorita_1ab99f
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_favoritar_loterica_9b9006)
            },
            Modifier.padding(top = 1.dp).size(20.dp)
        )
    }
}
