package br.gov.caixa.loterias.feature.cart.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSCircularProgressIndicator as CircularProgressIndicator
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSIcon as Icon
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSSurface as Surface
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.CaixaDSRecurringIcon
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.feature.cart.R
import br.gov.caixa.loterias.feature.cart.R as LocalizedR
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCart
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteBetSummaryText
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteNameSize
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteSummaryModalityName
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteTextStyle
import br.gov.caixa.loterias.feature.cart.presentation.mapper.label

@Composable
internal fun FavoriteCartRow(
    favorite: FavoriteCart,
    bets: List<FavoriteCartBet>,
    expanded: Boolean,
    loading: Boolean,
    onToggle: () -> Unit,
    onTransform: () -> Unit,
    onDetail: () -> Unit,
    onDelete: () -> Unit,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    val nameSize = favoriteNameSize(favorite.name)
    val nameHeight = favoriteNameHeight(favorite.name, nameSize)
    val rowHeight = maxOf(70.dp, nameHeight + 20.dp)
    val summaryTop = (rowHeight + nameHeight) / 2 + 10.dp
    val background by animateColorAsState(
        if (expanded) CaixaDSColorTokens.FavoriteCartExpanded else CaixaDSColorTokens.FavoriteCartCard,
        animationSpec = tween(100),
        label = uiStrings.getString(LocalizedR.string.ui_favorite_cart_background_096634)
    )
    Surface(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        color = background,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            Modifier.fillMaxWidth().uiId("cart.0072").clickable(onClick = onToggle).semantics {
                stateDescription = if (expanded) "Expandido" else "Recolhido"
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(rowHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = favorite.name,
                    modifier = (Modifier.padding(horizontal = 10.dp).width(150.dp)).uiId("cart.0073"),
                    color = CaixaDSColorTokens.White,
                    style = favoriteTextStyle.copy(fontSize = nameSize)

                )
                Spacer(Modifier.weight(1f))
                FavoriteCartRasterAction(
                    br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_icon_carrinho,
                    uiStrings.getString(LocalizedR.string.ui_transformar_carrinho_8fce5c),
                    onTransform
                )
                Spacer(Modifier.width(10.dp))
                FavoriteCartRasterAction(
                    br.gov.caixa.loterias.core.designsystem.R.drawable.legacy_ic_lupa_carrinho_fav,
                    uiStrings.getString(LocalizedR.string.ui_detalhar_carrinho_c36d88),
                    onDetail
                )
                FavoriteCartRasterAction(
                    R.drawable.legacy_favorite_ic_icone_lixeira,
                    uiStrings.getString(LocalizedR.string.ui_excluir_carrinho_b2cf91),
                    onDelete
                )
            }
            AnimatedVisibility(
                visible = expanded,
                modifier = Modifier.padding(top = summaryTop),
                enter = fadeIn(tween(100)),
                exit = fadeOut(tween(100))
            ) {
                Column(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {
                    bets.forEach { FavoriteCartBetSummary(it) }
                }
            }
            if (loading) {
                Box(Modifier.fillMaxWidth().padding(top = 70.dp).height(30.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = CaixaDSColorTokens.White,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteCartRasterAction(resource: Int, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.width(40.dp).height(70.dp).clip(RoundedCornerShape(4.dp)).uiId("cart.0074").clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(resource),
            description,
            (Modifier.size(20.dp)).uiId("cart.0075"),
            tint = CaixaDSColorTokens.White
        )
    }
}

@Composable
private fun FavoriteCartBetSummary(bet: FavoriteCartBet) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            favoriteSummaryModalityName(bet),
            modifier = (Modifier.weight(1f)).uiId("cart.0076"),
            color = CaixaDSColorTokens.White,
            style = favoriteTextStyle.copy(fontFamily = CaixaDSFuturaFontFamily, fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(Modifier.padding(end = 5.dp).size(20.dp)) {
            if (bet.recurringDraws > 0) {
                Icon(
                    CaixaDSRecurringIcon,
                    uiStrings.getString(LocalizedR.string.ui_teimosinha_dba980),
                    (Modifier.fillMaxSize()).uiId("cart.0077"),
                    tint = CaixaDSColorTokens.White
                )
            }
        }
        Text(
            favoriteBetSummaryText(bet),
            color = CaixaDSColorTokens.White,
            style = favoriteTextStyle,
            modifier = (Modifier.padding(5.dp)).uiId("cart.0078"),
            maxLines = 1
        )
        Spacer(Modifier.width(50.dp))
    }
}

@Composable
private fun favoriteNameHeight(name: String, nameSize: androidx.compose.ui.unit.TextUnit): androidx.compose.ui.unit.Dp {
    val measurer = rememberTextMeasurer()
    return with(LocalDensity.current) {
        measurer.measure(
            name,
            favoriteTextStyle.copy(fontSize = nameSize),
            maxLines = Int.MAX_VALUE,
            softWrap = true,
            constraints = androidx.compose.ui.unit.Constraints(maxWidth = 150.dp.roundToPx())
        ).size.height.toDp()
    }
}
