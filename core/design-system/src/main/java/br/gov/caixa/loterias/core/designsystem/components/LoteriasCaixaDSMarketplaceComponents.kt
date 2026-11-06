package br.gov.caixa.loterias.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens

/** Apresentação literal de activity_bolao/item_list_bolao/ver_mais_bolao, compartilhada pelos consumidores. */
object LoteriasCaixaDSMarketplaceStyle {
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
        fontFamily = LoteriasCaixaDSFontFamily,
        fontSize = size.sp,
        fontWeight = weight,
        platformStyle = PlatformTextStyle(includeFontPadding = true)
    )
}

/** O degradê nativo abrange o conteúdo da Activity, incluindo sua barra de ferramentas e o rodapé do carrinho. */
fun Modifier.loteriasCaixaDSMarketplaceCanvas(cartVisible: Boolean): Modifier = drawWithCache {
    val brush = Brush.verticalGradient(
        listOf(Color.White, LoteriasCaixaDSMarketplaceStyle.gradientEnd),
        startY = -56.dp.toPx(),
        endY = size.height + if (cartVisible) 36.dp.toPx() else 0f
    )
    onDrawBehind { drawRect(brush) }
}

@Composable
fun LoteriasCaixaDSMarketplaceTab(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val largeFont = LocalDensity.current.fontScale > 1f
    Box(
        modifier.fillMaxHeight().background(
            if (selected) LoteriasCaixaDSMarketplaceStyle.tab else LoteriasCaixaDSMarketplaceStyle.inactiveTab
        )
            .uiId("ds.0022").selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            modifier = if (largeFont) Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp) else Modifier,
            textAlign = if (largeFont) TextAlign.Center else null,
            color = if (selected) Color.White else LoteriasCaixaDSMarketplaceStyle.tab,
            style = LoteriasCaixaDSMarketplaceStyle.text(MarketplaceComponentsMetrics.LOTTERY_TITLE_SIZE_SP, FontWeight.SemiBold)
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
fun LoteriasCaixaDSMarketplaceToolbarIcon(help: Boolean, description: String, modifier: Modifier = Modifier, search: Boolean = false) {
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
fun LoteriasCaixaDSMarketplaceFavoriteRetailer(name: String, city: String, onOpen: () -> Unit, onRemove: () -> Unit) {
    if (LocalDensity.current.fontScale > 1f) {
        LoteriasCaixaDSLargeFontFavoriteRetailer(name, city, onOpen, onRemove)
        return
    }
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().background(Color.White)) {
        Box(Modifier.fillMaxWidth()) {
            Column(
                Modifier.fillMaxWidth(
                    MarketplaceComponentsMetrics.LOTTERY_DETAILS_WIDTH_FRACTION
                ).padding(start = 20.dp, top = 16.dp, bottom = 20.dp)
            ) {
                Text(
                    name,
                    color = LoteriasCaixaDSColorTokens.RetailSearchText,
                    style = LoteriasCaixaDSMarketplaceStyle.text(MarketplaceComponentsMetrics.LOTTERY_BODY_SIZE_SP, FontWeight.Bold)
                )
                Text(
                    city,
                    Modifier.padding(top = 6.dp),
                    color = LoteriasCaixaDSColorTokens.RetailSearchText,
                    style = LoteriasCaixaDSMarketplaceStyle.text(
                        MarketplaceComponentsMetrics.LOTTERY_BODY_SIZE_SP
                    ).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily)
                )
            }
            Row(Modifier.align(Alignment.CenterEnd).offset(y = 8.dp).padding(end = 40.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    uiStrings.getString(LocalizedR.string.ui_ver_boloes_30719c),
                    Modifier.border(1.dp, LoteriasCaixaDSColorTokens.CaixaBlue, RoundedCornerShape(4.dp))
                        .uiId("ds.0025").clickable(onClick = onOpen).padding(4.dp),
                    color = LoteriasCaixaDSColorTokens.CaixaBlue,
                    style = LoteriasCaixaDSMarketplaceStyle.text(MarketplaceComponentsMetrics.LOTTERY_BODY_SIZE_SP)
                )
                Spacer(Modifier.width(37.dp))
                Image(
                    painterResource(R.drawable.legacy_marketplace_trash),
                    uiStrings.getString(LocalizedR.string.ui_excluir_1_s_5fd47f, name),
                    Modifier.uiId("ds.0026").clickable(onClick = onRemove)
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(LoteriasCaixaDSMarketplaceStyle.divider))
    }
}

/** BolaoFooterAdapter nativo somado ao preenchimento inferior de conteúdo de 88dp do RecyclerView. */
@Composable
fun LoteriasCaixaDSMarketplaceListFooter(loading: Boolean, exhausted: Boolean) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (loading) androidx.compose.material3.CircularProgressIndicator(color = LoteriasCaixaDSColorTokens.CaixaBlue)
        if (exhausted) {
            Text(
                uiStrings.getString(LocalizedR.string.ui_fim_da_lista_43a7d9),
                Modifier.padding(top = 12.dp),
                color = LoteriasCaixaDSColorTokens.SecondaryText,
                style = LoteriasCaixaDSMarketplaceStyle.text(
                    MarketplaceComponentsMetrics.LOTTERY_BODY_SIZE_SP
                ).copy(fontFamily = LoteriasCaixaDSFuturaFontFamily)
            )
        }
    }
}

@Composable
fun LoteriasCaixaDSMarketplaceBack(onClick: () -> Unit) {
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

private object MarketplaceComponentsMetrics {
    const val LOTTERY_TITLE_SIZE_SP = 16
    const val LOTTERY_DETAILS_WIDTH_FRACTION = .56f
    const val LOTTERY_BODY_SIZE_SP = 14
}
