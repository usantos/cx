package br.gov.caixa.loterias.feature.marketplace.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSIcons
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.marketplace.R as LocalizedR
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.marketplaceListRetailerName

@Composable
internal fun DetailRetailerData(
    detail: MarketplacePoolDetail,
    favoriteLottery: Boolean?,
    isUpdatingFavorite: Boolean,
    onFavorite: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                marketplaceListRetailerName(detail.lotteryName),
                style = detailText(18, FontWeight.SemiBold),
                color = CaixaDSColorTokens.RetailSearchText,
                modifier = Modifier.uiId("pool.0074")
            )
            if (detail.lotteryNumber.isNotBlank()) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_codigo_1_s_772825, detail.lotteryNumber),
                    style = detailText(18, FontWeight.SemiBold),
                    color = CaixaDSColorTokens.RetailSearchText,
                    modifier = Modifier.uiId("pool.0075")
                )
            }
            if (detail.cityAndState.isNotBlank()) {
                Text(
                    detail.cityAndState,
                    style = detailText(18, FontWeight.SemiBold),
                    color = CaixaDSColorTokens.RetailSearchText,
                    modifier = Modifier.uiId("pool.0076")
                )
            }
        }
        Column(
            Modifier.width(80.dp).align(Alignment.CenterVertically).offset(y = (-8).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-10).dp)
        ) {
            favoriteLottery?.let { favorite ->
                Box(
                    Modifier.size(48.dp)
                        .uiId("pool.0077").clickable(enabled = !isUpdatingFavorite, role = Role.Button, onClick = onFavorite),
                    Alignment.Center
                ) {
                    Icon(
                        if (favorite) CaixaDSIcons.Favorite else CaixaDSIcons.FavoriteBorder,
                        if (favorite) {
                            uiStrings.getString(
                                LocalizedR.string.ui_excluir_loterica_favorita_1ab99f
                            )
                        } else {
                            uiStrings.getString(LocalizedR.string.ui_favoritar_loterica_9b9006)
                        },
                        (Modifier.size(28.dp)).uiId("pool.0078"),
                        tint = CaixaDSColorTokens.MarketplaceFavorite
                    )
                }
                Text(
                    if (favorite) {
                        uiStrings.getString(LocalizedR.string.ui_loterica_favorita_3fe384)
                    } else {
                        uiStrings.getString(
                            LocalizedR.string.ui_favoritar_loterica_91a291
                        )
                    },
                    (Modifier.fillMaxWidth()).uiId("pool.0079"),
                    style = detailText(16, FontWeight.SemiBold),
                    color = CaixaDSColorTokens.MarketplaceFavorite,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
