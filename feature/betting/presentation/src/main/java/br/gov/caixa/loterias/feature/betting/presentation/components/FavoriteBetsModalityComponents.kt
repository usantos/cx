package br.gov.caixa.loterias.feature.betting.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.gov.caixa.loterias.core.designsystem.artwork.CaixaDSLotteryArtwork
import br.gov.caixa.loterias.core.designsystem.artwork.caixaDSCampaignDrawable
import br.gov.caixa.loterias.core.designsystem.automation.uiId
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText as Text
import br.gov.caixa.loterias.core.designsystem.icon.caixaDSLotteryClover
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFontFamily
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSFuturaFontFamily
import br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSColorTokens
import br.gov.caixa.loterias.core.designsystem.tokens.caixaDSLotteryStyle
import br.gov.caixa.loterias.feature.betting.R
import br.gov.caixa.loterias.feature.betting.R as LocalizedR

@Composable
internal fun FavoriteModalityGrid(modalities: List<Pair<Int?, String>>, selected: Int?, onSelect: (Int?) -> Unit) {
    modalities.chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth()) {
            row.forEach { (id, name) ->
                val style = caixaDSLotteryStyle(id ?: 0, name)
                val isSelected = selected == id
                val selectedColor = if (id == null) CaixaDSColorTokens.ComboDark else style.light
                val background = if (isSelected) selectedColor else CaixaDSColorTokens.FavoriteFilterCanvas
                val border = if (isSelected) selectedColor else CaixaDSColorTokens.DrawerDivider
                val content = when {
                    !isSelected -> CaixaDSColorTokens.TicketChoiceMuted
                    id == null -> CaixaDSColorTokens.White
                    else -> style.content
                }
                Row(
                    Modifier.weight(1f).padding(4.dp).heightIn(min = 62.dp)
                        .background(background, RoundedCornerShape(8.dp))
                        .border(1.dp, border, RoundedCornerShape(8.dp))
                        .uiId("bet.0119").selectable(selected = isSelected, role = Role.RadioButton) { onSelect(id) }
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (id != null) Image(caixaDSLotteryClover(id, onDark = isSelected), null, Modifier.size(20.dp))
                    Text(
                        if (id == null) name else name.lowercase(),
                        (Modifier.weight(1f).padding(horizontal = 4.dp)).uiId("bet.0120"),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CaixaDSFuturaFontFamily,
                        color = content,
                        style = favoriteLegacyTextStyle
                    )
                }
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}

internal val favoriteLegacyTextStyle =
    TextStyle(fontFamily = CaixaDSFontFamily, platformStyle = PlatformTextStyle(includeFontPadding = true))

@Composable
internal fun FavoriteTrapezoid(id: Int, modifier: Modifier) {
    val resource = caixaDSCampaignDrawable(id, role = CaixaDSLotteryArtwork.TRAPEZE) ?: when (id) {
        3 -> R.drawable.legacy_favorite_trapeze_quina_mkp
        7 -> R.drawable.legacy_favorite_trapeze_super_sete_mkp
        8 -> R.drawable.legacy_favorite_trapeze_lotofacil_mkp
        9 -> R.drawable.legacy_favorite_trapeze_milionaria_mkp
        11 -> R.drawable.legacy_favorite_trapeze_dia_sorte_mkp
        16 -> R.drawable.legacy_favorite_trapeze_lotomanial_mkp
        18 -> R.drawable.legacy_favorite_trapeze_dupla_sena_mkp
        19 -> R.drawable.legacy_favorite_trapeze_loteca_mkp
        20 -> R.drawable.legacy_favorite_trapeze_timemania_mkp
        else -> R.drawable.legacy_favorite_trapeze_megasena_mkp
    }
    val context = LocalContext.current
    val resources = LocalResources.current
    val configuration = LocalConfiguration.current
    val drawable =
        remember(resource, resources, configuration, context.theme) { requireNotNull(resources.getDrawable(resource, context.theme)) }
    Canvas(modifier.clipToBounds()) {
        drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
        drawIntoCanvas { drawable.draw(it.nativeCanvas) }
    }
}

@Composable
internal fun FavoriteSuperSevenColumns(
    columns: List<List<Int>>,
    style: br.gov.caixa.loterias.core.designsystem.tokens.CaixaDSLotteryStyle,
) {
    val uiStrings = androidx.compose.ui.platform.LocalResources.current
    Row {
        columns.forEachIndexed { index, numbers ->
            Column(Modifier.width(41.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.padding(top = 8.dp).size(25.dp).background(style.light, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiStrings.getString(LocalizedR.string.ui_1_s_d020a2, (index + 1).toString()),
                        color = style.content,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CaixaDSFuturaFontFamily,
                        style = favoriteLegacyTextStyle,
                        modifier = Modifier.uiId("bet.0121")
                    )
                }
                Column(Modifier.padding(top = 7.dp)) {
                    numbers.sorted().forEach { number ->
                        Box(
                            Modifier.size(
                                30.dp
                            ).background(
                                CaixaDSColorTokens.White,
                                RoundedCornerShape(15.dp)
                            ).border(1.dp, style.dark, RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                number.toString(),
                                color = style.content,
                                fontSize = 14.sp,
                                style = favoriteLegacyTextStyle,
                                modifier = Modifier.uiId("bet.0122")
                            )
                        }
                    }
                }
            }
        }
    }
}
