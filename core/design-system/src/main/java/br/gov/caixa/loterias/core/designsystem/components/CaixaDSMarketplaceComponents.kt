package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.R
import br.gov.caixa.loterias.core.designsystem.R as LocalizedR
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens

/** Apresentação literal de activity_bolao/item_list_bolao/ver_mais_bolao, compartilhada pelos consumidores. */
object CaixaDSMarketplaceStyle {
    val tab = Color(0xFF00437A)
    val inactiveTab = Color(0xFFEFF5F6)
    val gradientEnd = Color(0xFFD4DFE2)
    val arrow = Color(0xFF404B52)
    val toolbarActionWidth = 41.dp
    val toolbarEndInset = 7.dp
    val contestMetadataOffset = 2.dp
    val listBottomPadding = 88.dp
    val divider = Color(0xFFD0E0E3)
    fun text(size: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
        fontFamily = CaixaDSFontFamily,
        fontSize = size.sp,
        fontWeight = weight,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
}

/** O degradê nativo abrange o conteúdo da Activity, incluindo sua barra de ferramentas e o rodapé do carrinho. */
fun Modifier.caixaDSMarketplaceCanvas(cartVisible: Boolean): Modifier = drawWithCache {
    val brush = Brush.verticalGradient(
        listOf(Color.White, CaixaDSMarketplaceStyle.gradientEnd),
        startY = -56.dp.toPx(),
        endY = size.height + if (cartVisible) 36.dp.toPx() else 0f
    )
    onDrawBehind { drawRect(brush) }
}

@Composable
fun CaixaDSMarketplaceTab(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Box(
        modifier.fillMaxHeight().background(if (selected) CaixaDSMarketplaceStyle.tab else CaixaDSMarketplaceStyle.inactiveTab)
            .uiId("ds.0022").selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            modifier = if (largeFont) Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp) else Modifier,
            textAlign = if (largeFont) TextAlign.Center else null,
            color = if (selected) Color.White else CaixaDSMarketplaceStyle.tab,
            style = CaixaDSMarketplaceStyle.text(16, FontWeight.SemiBold)
        )
        if (selected) {
            Image(
                painterResource(R.drawable.legacy_marketplace_tab_pointer),
                null,
                Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun CaixaDSMarketplaceToolbarIcon(help: Boolean, description: String, modifier: Modifier = Modifier, search: Boolean = false) {
    Image(
        painterResource(
            if (help) {
                R.drawable.legacy_marketplace_help
            } else if (search) {
                R.drawable.legacy_marketplace_search
            } else {
                R.drawable.legacy_marketplace_filter
            }
        ),
        description,
        modifier.size(25.dp)
    )
}

@Composable
fun CaixaDSMarketplacePoolSummary(
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
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    var priceBlockWidth by remember { mutableIntStateOf(0) }
    val reservedPriceWidth = with(LocalDensity.current) { priceBlockWidth.toDp() }
    Column(Modifier.fillMaxWidth().background(Color.White).padding(top = 16.dp)) {
        Box(Modifier.fillMaxWidth().uiId("ds.0023").clickable(onClick = onToggle)) {
            Column(Modifier.fillMaxWidth().padding(start = if (lotteryFiltered) 24.dp else 56.dp, end = 16.dp)) {
                Text(
                    title,
                    Modifier.fillMaxWidth(),
                    color = if (lotteryFiltered) modalityColor else CaixaDSColorTokens.RetailSearchText,
                    style = CaixaDSMarketplaceStyle.text(16, FontWeight.SemiBold)
                )
                if (lotteryFiltered) {
                    if (prize.isNotBlank()) Text(prize, color = CaixaDSMarketplaceStyle.arrow, style = CaixaDSMarketplaceStyle.text(16))
                    if (prizeWords.isNotBlank()) {
                        Text(
                            prizeWords,
                            color = CaixaDSMarketplaceStyle.arrow,
                            style = CaixaDSMarketplaceStyle.text(12)
                        )
                    }
                } else {
                    Text(city, color = CaixaDSMarketplaceStyle.arrow, style = CaixaDSMarketplaceStyle.text(16))
                }
                if (!expanded) {
                    Text(
                        summary,
                        Modifier.fillMaxWidth().padding(end = (reservedPriceWidth - 16.dp).coerceAtLeast(0.dp)),
                        color = CaixaDSMarketplaceStyle.arrow,
                        style = CaixaDSMarketplaceStyle.text(14).copy(fontFamily = CaixaDSFuturaFontFamily)
                    )
                }
            }
            // O bloco nativo de preço fica centralizado em todo o resumo da lotérica,
            // em vez de ocupar uma nova linha abaixo do nome da lotérica.
            Row(
                Modifier.align(Alignment.CenterEnd).onSizeChanged {
                    priceBlockWidth = it.width
                }.padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_valor_da_cota_7ed993),
                        color = CaixaDSMarketplaceStyle.arrow,
                        style = CaixaDSMarketplaceStyle.text(16)
                    )
                    Text(amount, color = CaixaDSColorTokens.RetailSearchText, style = CaixaDSMarketplaceStyle.text(16, FontWeight.Bold))
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
                    Modifier.size(23.dp, 15.dp).rotate(if (expanded) 180f else 0f)
                )
            }
            if (!lotteryFiltered) {
                MarketplaceFavoriteMarker(favorite, canFavorite, onFavorite)
            }
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSMarketplaceStyle.divider))
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
fun CaixaDSMarketplaceFavoriteRetailer(name: String, city: String, onOpen: () -> Unit, onRemove: () -> Unit) {
    if (LocalDensity.current.fontScale > 1f) {
        CaixaDSLargeFontFavoriteRetailer(name, city, onOpen, onRemove)
        return
    }
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().background(Color.White)) {
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(.56f).padding(start = 20.dp, top = 16.dp, bottom = 20.dp)) {
                Text(name, color = CaixaDSColorTokens.RetailSearchText, style = CaixaDSMarketplaceStyle.text(14, FontWeight.Bold))
                Text(
                    city,
                    Modifier.padding(top = 6.dp),
                    color = CaixaDSColorTokens.RetailSearchText,
                    style = CaixaDSMarketplaceStyle.text(14).copy(fontFamily = CaixaDSFuturaFontFamily)
                )
            }
            Row(Modifier.align(Alignment.CenterEnd).offset(y = 8.dp).padding(end = 40.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_ver_boloes_30719c),
                    Modifier.border(1.dp, CaixaDSColorTokens.CaixaBlue, RoundedCornerShape(4.dp))
                        .uiId("ds.0025").clickable(onClick = onOpen).padding(4.dp),
                    color = CaixaDSColorTokens.CaixaBlue,
                    style = CaixaDSMarketplaceStyle.text(14)
                )
                Spacer(Modifier.width(37.dp))
                Image(
                    painterResource(R.drawable.legacy_marketplace_trash),
                    uiStrings.getString(LocalizedR.string.ui_excluir_1_s_5fd47f, name),
                    Modifier.uiId("ds.0026").clickable(onClick = onRemove)
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(CaixaDSMarketplaceStyle.divider))
    }
}

@Composable
fun CaixaDSMarketplacePoolExpansion(
    contest: String? = null,
    prognostics: androidx.compose.ui.text.AnnotatedString,
    availability: androidx.compose.ui.text.AnnotatedString,
    shares: Int,
    canDecrease: Boolean = shares > 1,
    canIncrease: Boolean,
    adding: Boolean = false,
    canAdd: Boolean = shares > 0,
    color: Color,
    contentColor: Color,
    detailColor: Color,
    onDetail: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onAdd: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().background(Color.White), horizontalAlignment = Alignment.CenterHorizontally) {
        contest?.let { Text(it, color = detailColor, style = CaixaDSMarketplaceStyle.text(16, FontWeight.SemiBold)) }
        Text(
            prognostics,
            Modifier.padding(top = 12.dp),
            color = CaixaDSColorTokens.TicketChoiceMuted,
            style = CaixaDSMarketplaceStyle.text(16)
        )
        Box(
            Modifier.padding(top = 16.dp).size(88.dp, 28.dp).border(1.dp, detailColor, RoundedCornerShape(4.dp))
                .uiId("ds.0027").clickable(onClick = onDetail),
            contentAlignment = Alignment.Center
        ) {
            Text(uiStrings.getString(LocalizedR.string.ui_detalhes_62c950), color = detailColor, style = CaixaDSMarketplaceStyle.text(14))
        }
        Text(
            availability,
            Modifier.padding(top = 16.dp),
            color = CaixaDSColorTokens.TicketChoiceMuted,
            style = CaixaDSMarketplaceStyle.text(18)
        )
        Text(
            uiStrings.getString(LocalizedR.string.ui_quantidades_de_cotas_434de0),
            Modifier.align(Alignment.Start).padding(start = 16.dp, top = 19.dp),
            color = CaixaDSColorTokens.TicketChoiceMuted,
            style = CaixaDSMarketplaceStyle.text(18)
        )
        CaixaDSMarketplacePurchaseControls(
            shares, canDecrease && !adding, canIncrease && !adding, adding, canAdd && !adding,
            color, contentColor, onDecrease, onIncrease, onAdd
        )
    }
}

/** BolaoFooterAdapter nativo somado ao preenchimento inferior de conteúdo de 88dp do RecyclerView. */
@Composable
fun CaixaDSMarketplaceListFooter(loading: Boolean, exhausted: Boolean) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (loading) androidx.compose.material3.CircularProgressIndicator(color = CaixaDSColorTokens.CaixaBlue)
        if (exhausted) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_fim_da_lista_43a7d9),
                Modifier.padding(top = 12.dp),
                color = CaixaDSColorTokens.SecondaryText,
                style = CaixaDSMarketplaceStyle.text(14).copy(fontFamily = CaixaDSFuturaFontFamily)
            )
        }
    }
}

@Composable
fun CaixaDSMarketplaceBack(onClick: () -> Unit) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Box(Modifier.fillMaxSize().uiId("ds.0028").clickable(onClick = onClick)) {
        Image(
            painterResource(R.drawable.legacy_marketplace_back),
            uiStrings.getString(LocalizedR.string.ui_voltar_59dc92),
            Modifier.align(Alignment.CenterStart).offset(x = 22.dp).size(12.dp, 18.dp),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
        )
    }
}
