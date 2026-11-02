package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

@Composable
fun LoteriasCaixaDSMarketplacePoolSummary(
    title: String,
    city: String,
    summary: String,
    amount: String,
    lotteryFiltered: Boolean,
    prize: String,
    prizeWords: String,
    modalityColor: Color,
    expanded: Boolean = false,
    favorite: Boolean = false,
    canFavorite: Boolean,
    onFavorite: () -> Unit,
    onToggle: () -> Unit,
) {
    var priceBlockWidth by remember { mutableIntStateOf(0) }
    val reservedPriceWidth = with(LocalDensity.current) { priceBlockWidth.toDp() }
    Column(Modifier.fillMaxWidth().background(Color.White).padding(top = 16.dp)) {
        Box(Modifier.fillMaxWidth().uiId("ds.0023").clickable(onClick = onToggle)) {
            Column(Modifier.fillMaxWidth().padding(start = if (lotteryFiltered) 24.dp else 56.dp, end = 16.dp)) {
                Text(
                    title,
                    Modifier.fillMaxWidth(),
                    color = if (lotteryFiltered) modalityColor else LoteriasCaixaDSColorTokens.RetailSearchText,
                    style = LoteriasCaixaDSMarketplaceStyle.text(POOL_METADATA_TEXT_SIZE, FontWeight.SemiBold)
                )
                if (lotteryFiltered) {
                    if (prize.isNotBlank()) {
                        Text(
                            prize,
                            color = LoteriasCaixaDSMarketplaceStyle.arrow,
                            style = LoteriasCaixaDSMarketplaceStyle.text(POOL_METADATA_TEXT_SIZE)
                        )
                    }
                    if (prizeWords.isNotBlank()) {
                        Text(
                            prizeWords,
                            color = LoteriasCaixaDSMarketplaceStyle.arrow,
                            style = LoteriasCaixaDSMarketplaceStyle.text(PRIZE_DESCRIPTION_TEXT_SIZE)
                        )
                    }
                } else {
                    Text(
                        city,
                        color = LoteriasCaixaDSMarketplaceStyle.arrow,
                        style = LoteriasCaixaDSMarketplaceStyle.text(POOL_METADATA_TEXT_SIZE)
                    )
                }
                if (!expanded) {
                    Text(
                        summary,
                        Modifier.fillMaxWidth().padding(end = (reservedPriceWidth - 16.dp).coerceAtLeast(0.dp)),
                        color = LoteriasCaixaDSMarketplaceStyle.arrow,
                        style = LoteriasCaixaDSMarketplaceStyle.text(
                            BET_SUMMARY_TEXT_SIZE
                        ).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily)
                    )
                }
            }
            // O bloco nativo de preço fica centralizado em todo o resumo da lotérica,
            // em vez de ocupar uma nova linha abaixo do nome da lotérica.
            MarketplacePoolPrice(
                amount,
                expanded,
                Modifier.align(Alignment.CenterEnd).onSizeChanged {
                    priceBlockWidth = it.width
                }.padding(end = 16.dp)
            )
            if (!lotteryFiltered) {
                MarketplaceFavoriteMarker(favorite, canFavorite, onFavorite)
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDSMarketplaceStyle.divider))
    }
}

@Composable
private fun MarketplaceFavoriteMarker(favorite: Boolean, canFavorite: Boolean, onFavorite: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.padding(start = 24.dp).height(44.dp).width(24.dp), contentAlignment = Alignment.Center) {
        Image(
            painterResource(if (favorite) R.drawable.legacy_marketplace_favorite else R.drawable.legacy_marketplace_unfavorite),
            if (favorite) {
                uiStrings.getString(
                    LocalizedR.string.ui_excluir_loterica_favorita_1ab99f
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_favoritar_loterica_9b9006)
            },
            Modifier.size(24.dp, 21.dp).uiId("ds.0024").clickable(enabled = canFavorite, onClick = onFavorite)
        )
    }
}

@Composable
private fun MarketplacePoolPrice(amount: String, expanded: Boolean, modifier: Modifier) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_valor_da_cota_7ed993),
                color = LoteriasCaixaDSMarketplaceStyle.arrow,
                style = LoteriasCaixaDSMarketplaceStyle.text(POOL_METADATA_TEXT_SIZE)
            )
            Text(
                amount,
                color = LoteriasCaixaDSColorTokens.RetailSearchText,
                style = LoteriasCaixaDSMarketplaceStyle.text(POOL_METADATA_TEXT_SIZE, FontWeight.Bold)
            )
        }
        Spacer(Modifier.width(8.dp))
        Image(
            painterResource(R.drawable.legacy_marketplace_list_arrow),
            if (expanded) {
                uiStrings.getString(
                    LocalizedR.string.ui_recolher_bolao_82de6c
                )
            } else {
                uiStrings.getString(LocalizedR.string.ui_ver_mais_073f14)
            },
            Modifier.size(23.dp, 15.dp).rotate(if (expanded) EXPANDED_ARROW_ROTATION else 0f)
        )
    }
}

private const val POOL_METADATA_TEXT_SIZE = 16
private const val PRIZE_DESCRIPTION_TEXT_SIZE = 12
private const val BET_SUMMARY_TEXT_SIZE = 14
private const val EXPANDED_ARROW_ROTATION = 180f
